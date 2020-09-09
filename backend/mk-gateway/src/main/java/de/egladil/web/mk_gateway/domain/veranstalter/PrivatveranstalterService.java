// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.PrivatteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.PrivatveranstalterAPIModel;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * PrivatveranstalterService
 */
@ApplicationScoped
@DomainService
public class PrivatveranstalterService {

	private static final Logger LOG = LoggerFactory.getLogger(PrivatveranstalterService.class);

	private SecurityIncidentRegistered securityIncidentRegistered;

	private DataInconsistencyRegistered dataInconsistencyRegistered;

	@Inject
	VeranstalterRepository repository;

	@Inject
	ZugangUnterlagenService zugangUnterlagenService;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	PrivatteilnahmeKuerzelService teilnahmenKuerzelService;

	@Inject
	Event<SecurityIncidentRegistered> securityIncidentEvent;

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	public static PrivatveranstalterService createForTest(final VeranstalterRepository veranstalterRepository, final ZugangUnterlagenService zugangUnterlagenService, final WettbewerbService wettbewerbSerivice, final TeilnahmenRepository teilnahmenRepository, final PrivatteilnahmeKuerzelService teilnahmeKuerzelService) {

		PrivatveranstalterService result = new PrivatveranstalterService();
		result.repository = veranstalterRepository;
		result.zugangUnterlagenService = zugangUnterlagenService;
		result.wettbewerbService = wettbewerbSerivice;
		result.teilnahmenRepository = teilnahmenRepository;
		result.teilnahmenKuerzelService = teilnahmeKuerzelService;
		return result;
	}

	public PrivatveranstalterAPIModel findPrivatperson(final String uuid) {

		if (StringUtils.isBlank(uuid)) {

			throw new BadRequestException("uuid darf nicht blank sein.");
		}

		Optional<Veranstalter> optVeranstalter = this.repository.ofId(new Identifier(uuid));

		if (optVeranstalter.isEmpty()) {

			String msg = "Versuch, nicht vorhandenen Veranstalter mit UUID=" + uuid + " zu finden";
			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new NotFoundException("Kennen keinen Veranstalter mit dieser ID");
		}

		Veranstalter veranstalter = optVeranstalter.get();

		if (veranstalter.rolle() != Rolle.PRIVAT) {

			String msg = "Falsche Rolle: erwarten Privatveranstalter, war aber " + veranstalter.toString();
			LOG.warn(msg);
			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new NotFoundException("Kennen keinen Privatveranstalter mit dieser ID");
		}

		Optional<Wettbewerb> optWettbewerb = wettbewerbService.aktuellerWettbewerb();

		Privatveranstalter privatveranstalter = (Privatveranstalter) veranstalter;
		boolean hatZugang = false;

		if (optWettbewerb.isPresent()) {

			hatZugang = zugangUnterlagenService.hatZugang(privatveranstalter, optWettbewerb.get());
		}

		PrivatveranstalterAPIModel result = PrivatveranstalterAPIModel
			.create(hatZugang, privatveranstalter.isNewsletterEmpfaenger())
			.withTeilnahmenummer(privatveranstalter.persistierbareTeilnahmenummern());

		if (optWettbewerb.isPresent()) {

			List<Identifier> teilnahmenummern = privatveranstalter.teilnahmeIdentifier();

			if (teilnahmenummern.isEmpty() || teilnahmenummern.size() > 1) {

				String msg = "Bei der Migration der Privatkonten ist etwas schiefgegangen: " + privatveranstalter.toString()
					+ " hat "
					+ teilnahmenummern.size() + " Teilnahmenummern.";

				LOG.warn(msg);

				this.dataInconsistencyRegistered = new LoggableEventDelegate().fireDataInconsistencyEvent(msg,
					dataInconsistencyEvent);

				throw new MkGatewayRuntimeException("Kann aktuelle Teilnahme nicht ermitteln");
			}

			Identifier teilnahmenummer = teilnahmenummern.get(0);

			List<Teilnahme> teilnahmen = teilnahmenRepository.ofTeilnahmenummer(teilnahmenummer.identifier());

			WettbewerbID wettbewerbId = optWettbewerb.get().id();

			Optional<Teilnahme> optAktuelle = teilnahmen.stream().filter(t -> t.wettbewerbID().equals(wettbewerbId)).findFirst();

			if (optAktuelle.isPresent()) {

				Teilnahme aktuelle = optAktuelle.get();
				result.withTeilnahme(PrivatteilnahmeAPIModel.createFromPrivatteilnahme((Privatteilnahme) aktuelle));

			}

			result.withAnzahlTeilnahmen(teilnahmen.size());
		}

		return result;

	}

	/**
	 * Persistiert eine neue Privatveranstalter.
	 *
	 * @param data
	 *             CreateOrUpdatePrivatveranstalterCommand
	 */
	@Transactional
	public boolean addPrivatperson(final CreateOrUpdatePrivatveranstalterCommand data) {

		Optional<Veranstalter> optPrivatperson = repository.ofId(new Identifier(data.uuid()));

		if (optPrivatperson.isPresent()) {

			return false;
		}

		// Issue minikaenguru#18
		String teilnahmenummer = this.teilnahmenKuerzelService.neuesKuerzel();

		Person person = new Person(data.uuid(), data.fullName()).withEmail(data.email());
		Privatveranstalter privatveranstalter = new Privatveranstalter(person, data.newsletterEmpfaenger(),
			Arrays.asList(new Identifier(teilnahmenummer)));

		repository.addVeranstalter(privatveranstalter);
		return true;
	}

	SecurityIncidentRegistered getSecurityIncidentRegistered() {

		return securityIncidentRegistered;
	}

	DataInconsistencyRegistered getDataInconsistencyRegistered() {

		return dataInconsistencyRegistered;
	}
}
