// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.api.LehrerAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.events.LehrerChanged;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * LehrerService
 */
@ApplicationScoped
@DomainService
public class LehrerService {

	private static final Logger LOG = LoggerFactory.getLogger(LehrerService.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	SchulkollegienService schulkollegienService;

	@Inject
	ZugangUnterlagenService zugangUnterlagenService;

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	LoggableEventDelegate eventDelegate;

	public static LehrerService createServiceForTest(final VeranstalterRepository lehrerRepository, final SchulkollegienService schulkollegienService, final ZugangUnterlagenService zugangUnterlagenService, final WettbewerbService wettbewerbService) {

		LehrerService result = new LehrerService();
		result.veranstalterRepository = lehrerRepository;
		result.schulkollegienService = schulkollegienService;
		result.zugangUnterlagenService = zugangUnterlagenService;
		result.wettbewerbService = wettbewerbService;
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

		LehrerChanged lehrerChangedEventPayload = new LehrerChanged(person, "", neueSchulkuerzel, data.newsletterEmpfaenger());

		schulkollegienService.handleLehrerChanged(lehrerChangedEventPayload);

		if (domainEventHandler != null) {

			domainEventHandler.handleEvent(lehrerChangedEventPayload);
		} else {

			System.out.println(lehrerChangedEventPayload.serializeQuietly());
		}

		return true;

	}

	@Transactional
	public boolean changeLehrer(final CreateOrUpdateLehrerCommand data) {

		Optional<Veranstalter> optLehrer = veranstalterRepository.ofId(new Identifier(data.uuid()));

		if (optLehrer.isEmpty()) {

			String msg = "Versuch, einen nicht existierenden Lehrer zu ändern: " + data.toString();
			LOG.warn(msg);

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);
			return false;
		}

		Veranstalter veranstalter = optLehrer.get();

		if (Rolle.LEHRER != veranstalter.rolle()) {

			String msg = "Versuch, einen Veranstalter zu ändern, der kein Lehrer ist: " + data.toString() + " - " + veranstalter;
			LOG.warn(msg);

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);
			return false;
		}

		Lehrer vorhandener = (Lehrer) veranstalter;

		String alteSchulkuerzel = StringUtils.join(vorhandener.schulen(), ",");
		String neueSchulkuerzel = data.schulkuerzel();

		List<Identifier> schulen = new ArrayList<>();

		if (neueSchulkuerzel != null) {

			schulen = Arrays.stream(neueSchulkuerzel.split(",")).map(s -> new Identifier(s))
				.collect(Collectors.toList());
		}

		Lehrer geaenderterLehrer = new Lehrer(new Person(veranstalter.uuid(), data.fullName()), data.newsletterEmpfaenger(),
			schulen);

		veranstalterRepository.changeVeranstalter(geaenderterLehrer);

		LehrerChanged lehrerChangedEventPayload = new LehrerChanged(geaenderterLehrer.person(), alteSchulkuerzel, neueSchulkuerzel,
			data.newsletterEmpfaenger());

		if (domainEventHandler != null) {

			domainEventHandler.handleEvent(lehrerChangedEventPayload);
		} else {

			System.out.println(lehrerChangedEventPayload.serializeQuietly());
		}

		return true;
	}

	/**
	 * Fügt die gegebene Schule dem Lehrer hinzu und den Lehrer dem Schulkollegium.
	 *
	 * @param lehrerID
	 *                 Identifier
	 * @param schuleID
	 *                 Identifier
	 */
	@Transactional
	public ResponsePayload addSchule(final Identifier lehrerID, final Identifier schuleID) {

		Optional<Veranstalter> optLehrer = veranstalterRepository.ofId(lehrerID);

		if (optLehrer.isEmpty()) {

			String msg = "Unbekannter Lehrer mit UUID=" + lehrerID + " versucht, Schule mit KUERZEL=" + schuleID + " hinzuzufügen.";
			LOG.warn(msg);

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);
			throw new NotFoundException();
		}

		Veranstalter veranstalter = optLehrer.get();

		if (veranstalter.rolle() != Rolle.LEHRER) {

			String msg = "Privatveranstalter mit UUID=" + lehrerID + " versucht, Schule mit KUERZEL=" + schuleID + " hinzuzufügen.";
			LOG.warn(msg);

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);
			throw new NotFoundException();
		}

		Lehrer lehrer = (Lehrer) veranstalter;
		String alteSchulkuerzel = lehrer.joinedSchulen();

		List<String> zugeordneteSchulkuerzel = alteSchulkuerzel == null ? new ArrayList<>()
			: Arrays.asList(StringUtils.split(alteSchulkuerzel, ","));

		if (zugeordneteSchulkuerzel.contains(schuleID.identifier())) {

			LOG.debug("Schule {} war dem Lehrer {} bereits zugeordnet", schuleID.identifier(), lehrer);
			return ResponsePayload.messageOnly(MessagePayload.warn(applicationMessages.getString("lehrer.schulen.add.warn")));
		}

		lehrer.addSchule(schuleID);
		String neueSchulkuerzel = lehrer.joinedSchulen();

		veranstalterRepository.changeVeranstalter(lehrer);

		Person person = lehrer.person();
		LehrerChanged lehrerChangedEventPayload = new LehrerChanged(person, alteSchulkuerzel, neueSchulkuerzel,
			lehrer.isNewsletterEmpfaenger());

		schulkollegienService.handleLehrerChanged(lehrerChangedEventPayload);

		if (domainEventHandler != null) {

			domainEventHandler.handleEvent(lehrerChangedEventPayload);
		} else {

			System.out.println(lehrerChangedEventPayload.serializeQuietly());
		}

		return ResponsePayload.messageOnly(MessagePayload.info(applicationMessages.getString("lehrer.schulen.add.success")));

	}

	/**
	 * Entfernt die gegebene Schule vom Lehrer und den Lehrer aus dem Schulkollegium.
	 *
	 * @param lehrerID
	 *                 Identifier
	 * @param schuleID
	 *                 Identifier
	 */
	@Transactional
	public ResponsePayload removeSchule(final Identifier lehrerID, final Identifier schuleID) {

		Optional<Veranstalter> optLehrer = veranstalterRepository.ofId(lehrerID);

		if (optLehrer.isEmpty()) {

			String msg = "Unbekannter Lehrer mit UUID=" + lehrerID + " versucht, sich von der Schule mit KUERZEL=" + schuleID
				+ " abzumelden.";
			LOG.warn(msg);

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);
			throw new NotFoundException();
		}

		Veranstalter veranstalter = optLehrer.get();

		if (veranstalter.rolle() != Rolle.LEHRER) {

			String msg = "Privatveranstalter mit UUID=" + lehrerID + " versucht, sich von der Schule mit KUERZEL=" + schuleID
				+ " abzumelden.";
			LOG.warn(msg);

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);
			throw new NotFoundException();
		}

		Lehrer lehrer = (Lehrer) veranstalter;
		String alteSchulkuerzel = lehrer.joinedSchulen();

		List<String> zugeordneteSchulkuerzel = Arrays.asList(StringUtils.split(alteSchulkuerzel, ","));

		if (!zugeordneteSchulkuerzel.contains(schuleID.identifier())) {

			String msg = "removeSchule(): Schule " + schuleID + " war dem Lehrer " + lehrerID
				+ " nicht zugeordnet.";
			LOG.warn(msg);

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);

			return ResponsePayload
				.messageOnly(MessagePayload.warn(applicationMessages.getString("lehrer.schulen.remove.nicht_registriert.warn")));
		}

		lehrer.removeSchule(schuleID);
		String neueSchulkuerzel = lehrer.joinedSchulen();

		veranstalterRepository.changeVeranstalter(lehrer);

		Person person = lehrer.person();
		LehrerChanged lehrerChangedEventPayload = new LehrerChanged(person, alteSchulkuerzel, neueSchulkuerzel,
			lehrer.isNewsletterEmpfaenger());

		schulkollegienService.handleLehrerChanged(lehrerChangedEventPayload);

		if (domainEventHandler != null) {

			domainEventHandler.handleEvent(lehrerChangedEventPayload);
		} else {

			System.out.println(lehrerChangedEventPayload.serializeQuietly());
		}

		return ResponsePayload.messageOnly(MessagePayload.info(applicationMessages.getString("lehrer.schulen.remove.success")));

	}

	/**
	 * @param  name
	 * @return
	 */
	public LehrerAPIModel findLehrer(final String uuid) {

		if (StringUtils.isBlank(uuid)) {

			throw new IllegalArgumentException("uuid darf nicht blank sein");
		}

		Optional<Veranstalter> optVeranstalter = this.veranstalterRepository.ofId(new Identifier(uuid));

		if (optVeranstalter.isEmpty()) {

			String msg = "Versuch, nicht vorhandenen Veranstalter mit UUID=" + uuid + " zu finden";
			LOG.warn(msg);

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);
			throw new NotFoundException("Kennen keinen Veranstalter mit dieser ID");
		}

		Veranstalter veranstalter = optVeranstalter.get();

		if (veranstalter.rolle() != Rolle.LEHRER) {

			String msg = "Falsche Rolle: erwarten Lehrer, war aber " + veranstalter.toString();
			LOG.warn(msg);
			eventDelegate.fireSecurityEvent(msg, domainEventHandler);
			throw new NotFoundException("Kennen keinen Lehrer mit dieser ID");
		}

		Lehrer lehrer = (Lehrer) veranstalter;
		boolean hatZugang = false;

		Optional<Wettbewerb> optWettbewerb = wettbewerbService.aktuellerWettbewerb();

		if (optWettbewerb.isPresent()) {

			hatZugang = zugangUnterlagenService.hatZugang(lehrer, optWettbewerb.get());
		}

		LehrerAPIModel result = LehrerAPIModel
			.create(hatZugang, lehrer.isNewsletterEmpfaenger()).withTeilnahmenummern(veranstalter.teilnahmenummernAsStrings());

		return result;
	}
}
