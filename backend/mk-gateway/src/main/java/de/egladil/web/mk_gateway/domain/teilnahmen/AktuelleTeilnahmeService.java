// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulanmeldungRequestPayload;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * AktuelleTeilnahmeService
 */
@ApplicationScoped
@DomainService
public class AktuelleTeilnahmeService {

	private static final Logger LOG = LoggerFactory.getLogger(AktuelleTeilnahmeService.class);

	@Inject
	Event<PrivatteilnahmeCreated> privatteilnahmeCreated;

	@Inject
	Event<SchulteilnahmeCreated> schulteilahmeCreatedEvent;

	@Inject
	Event<SecurityIncidentRegistered> securityIncidentEvent;

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	private PrivatteilnahmeCreated privatteilnahmeCreatedEvent;

	private SchulteilnahmeCreated schulteilnahmeCreated;

	private SecurityIncidentRegistered securityIncidentRegistered;

	private DataInconsistencyRegistered dataInconsistencyRegistered;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	static AktuelleTeilnahmeService createForTest(final TeilnahmenRepository teilnahmenRepository, final WettbewerbService wettbewerbService, final VeranstalterRepository veranstalterRepository) {

		AktuelleTeilnahmeService result = new AktuelleTeilnahmeService();
		result.teilnahmenRepository = teilnahmenRepository;
		result.wettbewerbService = wettbewerbService;
		result.veranstalterRepository = veranstalterRepository;
		return result;
	}

	/**
	 * Sucht die aktuelle Teilnahme mit der gegebenen teilnahmenummer.
	 *
	 * @param  teilnahmenummer
	 * @return                 boolean
	 */
	public Optional<Teilnahme> aktuelleTeilnahme(final String teilnahmenummer) {

		if (StringUtils.isBlank(teilnahmenummer)) {

			throw new BadRequestException("teilnahmenummer darf nicht blank sein.");
		}

		List<Teilnahme> teilnahmen = teilnahmenRepository.ofTeilnahmenummer(teilnahmenummer);

		return this.aktuelleTeilnahme(teilnahmen);
	}

	/**
	 * Wenn man irgendwo her die Teilnahmen bereits hat, ist das einfacher.
	 *
	 * @param  teilnahmen
	 *                    List
	 * @return            Optional
	 */
	public Optional<Teilnahme> aktuelleTeilnahme(final List<Teilnahme> teilnahmen) {

		if (teilnahmen == null) {

			throw new BadRequestException("teilnahmen darf nicht null sein.");
		}

		Optional<Wettbewerb> optLaufend = wettbewerbService.aktuellerWettbewerb();

		if (optLaufend.isEmpty()) {

			return Optional.empty();
		}

		final Wettbewerb laufenderWettbewerb = optLaufend.get();

		Optional<Teilnahme> optAktuelle = teilnahmen.stream().filter(t -> t.wettbewerbID().equals(laufenderWettbewerb.id()))
			.findFirst();

		return optAktuelle;

	}

	/**
	 * Legt eine neue Privatteilnahme an, wenn alle Voraussetzungen erfüllt sind.
	 *
	 * @param  uuid
	 *              String die UUID eines Veranstalters. Dies muss ein Privatmensch sein, der nicht gesperrt ist.
	 * @return      Teilnahme die neue oder einen möglicherweise bereits vorhandene.
	 */
	@Transactional
	public Privatteilnahme privatpersonAnmelden(final String uuid) {

		if (StringUtils.isBlank(uuid)) {

			throw new BadRequestException("uuid darf nicht blank sein.");
		}

		Wettbewerb aktuellerWettbewerb = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(identifier);

		if (optVeranstalter.isEmpty()) {

			String msg = "Jemand versucht als Veranstalter mit UUID=" + uuid
				+ " eine Privatteilnahme anzulegen, aber es gibt keinen Veranstalter mit dieser UUID.";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new AccessDeniedException("keinen Veranstalter mit UUID=" + uuid + " gefunden");
		}

		Veranstalter veranstalter = optVeranstalter.get();

		if (Rolle.LEHRER == veranstalter.rolle()) {

			String msg = veranstalter.toString() + " versucht, eine Privatteilnahme anzulegen.";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new AccessDeniedException("Der Veranstalter ist ein Lehrer. Nur Privatprsonen dürfen diese Funktion aufrufen.");
		}

		if (veranstalter.zugangUnterlagen() == ZugangUnterlagen.ENTZOGEN) {

			String msg = veranstalter.toString() + " hat keine Berechtigung zur Anmeldung: Zugang Unterlagen "
				+ veranstalter.zugangUnterlagen();

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new AccessDeniedException("Dem Veranstalter wurde der Zugang zu den Unterlagen entzogen.");
		}

		List<Identifier> teilnahmenummern = veranstalter.teilnahmeIdentifier();

		if (teilnahmenummern.isEmpty() || teilnahmenummern.size() > 1) {

			String msg = "Bei der Migration der Privatkonten ist etwas schiefgegangen: " + veranstalter.toString() + " hat "
				+ teilnahmenummern.size() + " Teilnahmenummern.";

			LOG.warn(msg);

			this.dataInconsistencyRegistered = new LoggableEventDelegate().fireDataInconsistencyEvent(msg,
				dataInconsistencyEvent);

			throw new MkGatewayRuntimeException("Kann aktuelle Teilnahme nicht ermitteln");
		}

		Identifier teilnahmenummer = teilnahmenummern.get(0);

		Optional<Teilnahme> optVorhandene = this.findVorhandeneTeilnahme(teilnahmenummer.identifier(), Teilnahmeart.PRIVAT,
			aktuellerWettbewerb);

		if (optVorhandene.isPresent()) {

			return (Privatteilnahme) optVorhandene.get();
		}

		Privatteilnahme neue = new Privatteilnahme(aktuellerWettbewerb.id(), veranstalter.teilnahmeIdentifier().get(0));

		teilnahmenRepository.addTeilnahme(neue);

		privatteilnahmeCreatedEvent = PrivatteilnahmeCreated.create(neue, uuid);

		if (privatteilnahmeCreated != null) {

			privatteilnahmeCreated.fire(privatteilnahmeCreatedEvent);
		}

		return neue;
	}

	/**
	 * @param  schulkuerzel
	 *                      String das Schulkuerzel
	 * @param  uuid
	 *                      String uuid des anmeldenden Lehrers.
	 * @return              SchulteilnahmeAPIModel
	 */
	@Transactional
	public SchulteilnahmeAPIModel schuleAnmelden(final SchulanmeldungRequestPayload payload, final String uuid) {

		if (payload == null) {

			throw new BadRequestException("payload darf nicht null sein.");
		}

		if (StringUtils.isBlank(uuid)) {

			throw new BadRequestException("uuid darf nicht blank sein.");
		}

		String schulkuerzel = payload.schulkuerzel();

		if (StringUtils.isBlank(schulkuerzel)) {

			throw new BadRequestException("schulkuerzel darf nicht blank sein.");
		}

		String schulname = payload.schulname();

		if (StringUtils.isBlank(schulname)) {

			throw new BadRequestException("schulname darf nicht blank sein.");
		}

		Wettbewerb aktuellerWettbewerb = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(identifier);

		if (optVeranstalter.isEmpty()) {

			String msg = "Jemand versucht als Veranstalter mit UUID=" + uuid
				+ " eine Schulteilnahme " + payload + " anzulegen, aber es gibt keinen Veranstalter mit dieser UUID.";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);

			throw new AccessDeniedException("keinen Veranstalter mit UUID=" + uuid + " gefunden");
		}

		Veranstalter veranstalter = optVeranstalter.get();

		if (Rolle.PRIVAT == veranstalter.rolle()) {

			String msg = veranstalter.toString() + " versucht, eine Schulteilnahme " + payload.toString() + " anzulegen.";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new AccessDeniedException("Dies ist ein Privatveranstalter. Nur Lehrer dürfen diese Funktion aufrufen.");
		}

		if (veranstalter.zugangUnterlagen() == ZugangUnterlagen.ENTZOGEN) {

			String msg = veranstalter.toString() + " hat keine Berechtigung zur Anmeldung: Zugang Unterlagen "
				+ veranstalter.zugangUnterlagen();

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);

			throw new AccessDeniedException("Dem Veranstalter wurde der Zugang zu den Unterlagen entzogen.");
		}

		List<Identifier> teilnahmenummern = veranstalter.teilnahmeIdentifier();

		long anzahlMitSchule = teilnahmenummern.stream().filter(n -> schulkuerzel.equals(n.identifier())).count();

		if (anzahlMitSchule == 0l) {

			String msg = veranstalter.toString() + " hat keine Berechtigung zur Anmeldung der Schule " + payload.toString()
				+ ", da er nicht für diese Schule registriert ist.";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);

			throw new AccessDeniedException("Der Lehrer gehört nicht zur anzumeldenden Schule.");

		}

		Optional<Teilnahme> optVorhandene = this.findVorhandeneTeilnahme(schulkuerzel, Teilnahmeart.SCHULE, aktuellerWettbewerb);

		Schulteilnahme schulteilnahme = null;

		if (optVorhandene.isPresent()) {

			schulteilnahme = (Schulteilnahme) optVorhandene.get();
		} else {

			schulteilnahme = new Schulteilnahme(aktuellerWettbewerb.id(), new Identifier(schulkuerzel), schulname,
				new Identifier(uuid));

			teilnahmenRepository.addTeilnahme(schulteilnahme);

			this.schulteilnahmeCreated = SchulteilnahmeCreated.create(schulteilnahme);

			if (schulteilahmeCreatedEvent != null) {

				this.schulteilahmeCreatedEvent.fire(schulteilnahmeCreated);
			} else {

				System.out.println(schulteilnahmeCreated.serializeQuietly());
			}
		}

		SchulteilnahmeAPIModel result = SchulteilnahmeAPIModel.create(schulteilnahme).withAngemeldetDurch(veranstalter.fullName());

		return result;

	}

	private Optional<Teilnahme> findVorhandeneTeilnahme(final String teilnahmekuerzel, final Teilnahmeart teilnahmeart, final Wettbewerb aktuellerWettbewerb) {

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart)
			.withTeilnahmenummer(teilnahmekuerzel).withWettbewerbID(aktuellerWettbewerb.id());
		return teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier);
	}

	PrivatteilnahmeCreated privatteilnahmeCreatedEvent() {

		return privatteilnahmeCreatedEvent;
	}

	SchulteilnahmeCreated schulteilnahmeCreated() {

		return schulteilnahmeCreated;
	}

	SecurityIncidentRegistered getSecurityIncidentRegistered() {

		return securityIncidentRegistered;
	}

	DataInconsistencyRegistered getDataInconsistencyRegistered() {

		return dataInconsistencyRegistered;
	}
}
