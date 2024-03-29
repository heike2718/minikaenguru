// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.PrivatteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulanmeldungRequestPayload;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.events.PrivatteilnahmeCreated;
import de.egladil.web.mk_gateway.domain.teilnahmen.events.SchulteilnahmeCreated;
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

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	LoggableEventDelegate eventDelegate;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	public static AktuelleTeilnahmeService createForTest(final TeilnahmenRepository teilnahmenRepository, final WettbewerbService wettbewerbService, final VeranstalterRepository veranstalterRepository) {

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
	 * @return      ResponsePayload die neue oder einen möglicherweise bereits vorhandene.
	 */
	@Transactional
	public ResponsePayload privatpersonAnmelden(final String uuid) {

		if (StringUtils.isBlank(uuid)) {

			throw new BadRequestException("uuid darf nicht blank sein.");
		}

		try {

			Wettbewerb aktuellerWettbewerb = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

			final Identifier identifier = new Identifier(uuid);

			Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(identifier);

			if (optVeranstalter.isEmpty()) {

				String msg = "Jemand versucht als Veranstalter mit UUID=" + uuid
					+ " eine Privatteilnahme anzulegen, aber es gibt keinen Veranstalter mit dieser UUID.";

				LOG.warn(msg);

				eventDelegate.fireSecurityEvent(msg, domainEventHandler);
				throw new AccessDeniedException("keinen Veranstalter mit UUID=" + uuid + " gefunden");
			}

			Veranstalter veranstalter = optVeranstalter.get();

			if (Rolle.LEHRER == veranstalter.rolle()) {

				String msg = veranstalter.toString() + " versucht, eine Privatteilnahme anzulegen.";

				LOG.warn(msg);

				eventDelegate.fireSecurityEvent(msg, domainEventHandler);
				throw new AccessDeniedException(
					"Der Veranstalter ist ein Lehrer. Nur Privatprsonen dürfen diese Funktion aufrufen.");
			}

			if (veranstalter.zugangUnterlagen() == ZugangUnterlagen.ENTZOGEN) {

				String msg = veranstalter.toString() + " hat keine Berechtigung zur Anmeldung: Zugang Unterlagen "
					+ veranstalter.zugangUnterlagen();

				LOG.warn(msg);

				eventDelegate.fireSecurityEvent(msg, domainEventHandler);
				throw new AccessDeniedException("Dem Veranstalter wurde der Zugang zu den Unterlagen entzogen.");
			}

			List<Identifier> teilnahmenummern = veranstalter.teilnahmeIdentifier();

			if (teilnahmenummern.isEmpty() || teilnahmenummern.size() > 1) {

				String msg = "Bei der Migration der Privatkonten ist etwas schiefgegangen: " + veranstalter.toString() + " hat "
					+ teilnahmenummern.size() + " Teilnahmenummern.";

				LOG.warn(msg);

				eventDelegate.fireDataInconsistencyEvent(msg,
					domainEventHandler);

				throw new MkGatewayRuntimeException("Kann aktuelle Teilnahme nicht ermitteln");
			}

			Identifier teilnahmenummer = teilnahmenummern.get(0);

			Optional<Teilnahme> optVorhandene = this.findVorhandeneTeilnahme(teilnahmenummer.identifier(), Teilnahmeart.PRIVAT,
				aktuellerWettbewerb);

			if (optVorhandene.isPresent()) {

				return new ResponsePayload(
					MessagePayload.info(applicationMessages.getString("teilnahmenResource.anmelden.privat.success")),
					PrivatteilnahmeAPIModel.createFromPrivatteilnahme((Privatteilnahme) optVorhandene.get()));
			}

			Privatteilnahme neue = new Privatteilnahme(aktuellerWettbewerb.id(), veranstalter.teilnahmeIdentifier().get(0));

			teilnahmenRepository.addTeilnahme(neue);

			if (domainEventHandler != null) {

				domainEventHandler.handleEvent(PrivatteilnahmeCreated.create(neue, uuid));
			}

			return new ResponsePayload(
				MessagePayload.info(applicationMessages.getString("teilnahmenResource.anmelden.privat.success")),
				PrivatteilnahmeAPIModel.createFromPrivatteilnahme(neue));

		} catch (IllegalStateException e) {

			return ResponsePayload.messageOnly(MessagePayload.warn(e.getMessage()));
		}
	}

	/**
	 * @param  schulkuerzel
	 *                      String das Schulkuerzel
	 * @param  uuid
	 *                      String uuid des anmeldenden Lehrers.
	 * @return              SchulteilnahmeAPIModel
	 */
	@Transactional
	public ResponsePayload schuleAnmelden(final SchulanmeldungRequestPayload payload, final String uuid) {

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

		try {

			Wettbewerb aktuellerWettbewerb = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

			final Identifier identifier = new Identifier(uuid);

			Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(identifier);

			if (optVeranstalter.isEmpty()) {

				String msg = "Jemand versucht als Veranstalter mit UUID=" + uuid
					+ " eine Schulteilnahme " + payload + " anzulegen, aber es gibt keinen Veranstalter mit dieser UUID.";

				LOG.warn(msg);

				eventDelegate.fireSecurityEvent(msg, domainEventHandler);

				throw new AccessDeniedException("keinen Veranstalter mit UUID=" + uuid + " gefunden");
			}

			Veranstalter veranstalter = optVeranstalter.get();

			if (Rolle.PRIVAT == veranstalter.rolle()) {

				String msg = veranstalter.toString() + " versucht, eine Schulteilnahme " + payload.toString() + " anzulegen.";

				LOG.warn(msg);

				eventDelegate.fireSecurityEvent(msg, domainEventHandler);
				throw new AccessDeniedException("Dies ist ein Privatveranstalter. Nur Lehrer dürfen diese Funktion aufrufen.");
			}

			if (veranstalter.zugangUnterlagen() == ZugangUnterlagen.ENTZOGEN) {

				String msg = veranstalter.toString() + " hat keine Berechtigung zur Anmeldung: Zugang Unterlagen "
					+ veranstalter.zugangUnterlagen();

				LOG.warn(msg);

				eventDelegate.fireSecurityEvent(msg, domainEventHandler);

				throw new AccessDeniedException("Dem Veranstalter wurde der Zugang zu den Unterlagen entzogen.");
			}

			List<Identifier> teilnahmenummern = veranstalter.teilnahmeIdentifier();

			long anzahlMitSchule = teilnahmenummern.stream().filter(n -> schulkuerzel.equals(n.identifier())).count();

			if (anzahlMitSchule == 0l) {

				String msg = veranstalter.toString() + " hat keine Berechtigung zur Anmeldung der Schule " + payload.toString()
					+ ", da er nicht für diese Schule registriert ist.";

				LOG.warn(msg);

				eventDelegate.fireSecurityEvent(msg, domainEventHandler);

				throw new AccessDeniedException("Der Lehrer gehört nicht zur anzumeldenden Schule.");

			}

			Optional<Teilnahme> optVorhandene = this.findVorhandeneTeilnahme(schulkuerzel, Teilnahmeart.SCHULE,
				aktuellerWettbewerb);

			Schulteilnahme schulteilnahme = null;

			if (optVorhandene.isPresent()) {

				schulteilnahme = (Schulteilnahme) optVorhandene.get();
			} else {

				schulteilnahme = new Schulteilnahme(aktuellerWettbewerb.id(), new Identifier(schulkuerzel), schulname,
					new Identifier(uuid));

				teilnahmenRepository.addTeilnahme(schulteilnahme);

				SchulteilnahmeCreated schulteilnahmeCreated = SchulteilnahmeCreated.create(schulteilnahme);

				if (domainEventHandler != null) {

					domainEventHandler.handleEvent(schulteilnahmeCreated);
				} else {

					System.out.println(schulteilnahmeCreated.serializeQuietly());
				}
			}

			SchulteilnahmeAPIModel result = SchulteilnahmeAPIModel.create(schulteilnahme)
				.withAngemeldetDurch(veranstalter.fullName());

			String message = MessageFormat.format(applicationMessages.getString("teilnahmenResource.anmelden.schule.success"),
				new Object[] { result.nameUrkunde() });

			return new ResponsePayload(
				MessagePayload.info(message),
				result);

		} catch (IllegalStateException e) {

			return ResponsePayload.messageOnly(MessagePayload.warn(e.getMessage()));

		}

	}

	private Optional<Teilnahme> findVorhandeneTeilnahme(final String teilnahmekuerzel, final Teilnahmeart teilnahmeart, final Wettbewerb aktuellerWettbewerb) {

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart)
			.withTeilnahmenummer(teilnahmekuerzel).withWettbewerbID(aktuellerWettbewerb.id());
		return teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier);
	}

}
