// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.api.LehrerAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.messaging.LoescheVeranstalterCommand;

/**
 * LehrerService
 */
@ApplicationScoped
@DomainService
public class LehrerService {

	private static final Logger LOG = LoggerFactory.getLogger(LehrerService.class);

	private SecurityIncidentRegistered securityIncidentRegistered;

	private DataInconsistencyRegistered dataInconsistencyRegistered;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	SchulkollegienService schulkollegienService;

	@Inject
	ZugangUnterlagenService zugangUnterlagenService;

	@Inject
	Event<SecurityIncidentRegistered> securityEventRegistered;

	@Inject
	Event<LehrerChanged> lehrerChanged;

	private LehrerChanged lehrerChangedEventPayload;

	private SecurityIncidentRegistered securityIncidentEventPayload;

	public static LehrerService createServiceForTest(final VeranstalterRepository lehrerRepository) {

		LehrerService result = new LehrerService();
		result.veranstalterRepository = lehrerRepository;
		return result;

	}

	@Transactional
	public boolean addLehrer(final CreateOrUpdateLehrerCommand data) {

		Optional<Veranstalter> optLehrer = veranstalterRepository.ofId(new Identifier(data.uuid()));

		if (optLehrer.isPresent()) {

			LOG.info("Es gibt bereits einen Lehrer: {}", optLehrer.get());

			return false;
		}
		String neueSchulkuerzel = data.schulkuerzel();

		List<Identifier> schulen = Arrays.stream(neueSchulkuerzel.split(",")).map(s -> new Identifier(s))
			.collect(Collectors.toList());
		Person person = new Person(data.uuid(), data.fullName()).withEmail(data.email());

		Lehrer lehrer = new Lehrer(person, data.newsletterEmpfaenger(), schulen);

		veranstalterRepository.addVeranstalter(lehrer);

		lehrerChangedEventPayload = new LehrerChanged(person, "", neueSchulkuerzel, data.newsletterEmpfaenger());

		if (lehrerChanged != null) {

			lehrerChanged.fire(lehrerChangedEventPayload);
		}

		return true;

	}

	public boolean changeLehrer(final CreateOrUpdateLehrerCommand data) {

		Optional<Veranstalter> optLehrer = veranstalterRepository.ofId(new Identifier(data.uuid()));

		if (optLehrer.isEmpty()) {

			String msg = "Versuch, einen nicht existierenden Lehrer zu ändern: " + data.toString();

			this.securityIncidentEventPayload = new SecurityIncidentRegistered(msg);
			new LoggableEventDelegate().fireSecurityEvent(msg, securityEventRegistered);

			LOG.warn(msg);

			return false;
		}

		Veranstalter veranstalter = optLehrer.get();

		if (Rolle.LEHRER != veranstalter.rolle()) {

			LOG.warn("Versuch, einen Veranstalter zu ändern, der kein Lehrer ist: {} - {}", data, veranstalter);

			return false;
		}

		Lehrer vorhandener = (Lehrer) veranstalter;

		String alteSchulkuerzel = StringUtils.join(vorhandener.schulen(), ",");
		String neueSchulkuerzel = data.schulkuerzel();

		List<Identifier> schulen = Arrays.stream(neueSchulkuerzel.split(",")).map(s -> new Identifier(s))
			.collect(Collectors.toList());

		Lehrer geaenderterLehrer = new Lehrer(new Person(veranstalter.uuid(), data.fullName()), data.newsletterEmpfaenger(),
			schulen);

		veranstalterRepository.changeVeranstalter(geaenderterLehrer);

		lehrerChangedEventPayload = new LehrerChanged(geaenderterLehrer.person(), alteSchulkuerzel, neueSchulkuerzel,
			data.newsletterEmpfaenger());

		if (lehrerChanged != null) {

			lehrerChanged.fire(lehrerChangedEventPayload);
		}

		return true;
	}

	LehrerChanged lehrerChangedEventPayload() {

		return lehrerChangedEventPayload;
	}

	SecurityIncidentRegistered securityIncidentEventPayload() {

		return securityIncidentEventPayload;
	}

	/**
	 * @param  name
	 * @return
	 */
	public LehrerAPIModel findLehrer(final String uuid) {

		if (StringUtils.isBlank(uuid)) {

			throw new BadRequestException("uuid darf nicht blank sein.");
		}

		Optional<Veranstalter> optVeranstalter = this.veranstalterRepository.ofId(new Identifier(uuid));

		if (optVeranstalter.isEmpty()) {

			String msg = "Versuch, nicht vorhandenen Veranstalter mit UUID=" + uuid + " zu finden";
			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityEventRegistered);
			throw new NotFoundException("Kennen keinen Veranstalter mit dieser ID");
		}

		Veranstalter veranstalter = optVeranstalter.get();

		if (veranstalter.rolle() != Rolle.LEHRER) {

			String msg = "Falsche Rolle: erwarten Lehrer, war aber " + veranstalter.toString();
			LOG.warn(msg);
			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityEventRegistered);
			throw new NotFoundException("Kennen keinen Lehrer mit dieser ID");
		}

		Lehrer lehrer = (Lehrer) veranstalter;
		boolean hatZugang = false;

		Optional<Wettbewerb> optWettbewerb = wettbewerbService.aktuellerWettbewerb();

		if (optWettbewerb.isPresent()) {

			hatZugang = zugangUnterlagenService.hatZugang(lehrer, optWettbewerb.get());
		}

		LehrerAPIModel result = LehrerAPIModel
			.create(hatZugang, lehrer.isNewsletterEmpfaenger());

		return result;
	}

	SecurityIncidentRegistered getSecurityIncidentRegistered() {

		return securityIncidentRegistered;
	}

	DataInconsistencyRegistered getDataInconsistencyRegistered() {

		return dataInconsistencyRegistered;
	}

	/**
	 * @param command
	 */
	public void lehrerAnonymisieren(final LoescheVeranstalterCommand command) {

		// TODO
	}

}
