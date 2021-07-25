// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.ActionNotAuthorizedException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadAuthorizationService;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VeranstalterHibernateRepository;

/**
 * UploadAuthorizationServiceImpl
 */
@ApplicationScoped
public class UploadAuthorizationServiceImpl implements UploadAuthorizationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadAuthorizationServiceImpl.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	public static UploadAuthorizationServiceImpl createForIntegrationTests(final EntityManager em) {

		UploadAuthorizationServiceImpl result = new UploadAuthorizationServiceImpl();
		result.wettbewerbService = WettbewerbService.createForIntegrationTest(em);
		result.veranstalterRepository = VeranstalterHibernateRepository.createForIntegrationTest(em);
		result.teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(em);
		result.loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(em);
		result.domainEventHandler = DomainEventHandler.createForIntegrationTest(em);
		return result;
	}

	@Override
	public boolean authorizeUpload(final Identifier benutzerID, final String schulkuerzel, final UploadType uploadType, final Rolle rolle) {

		if (Rolle.PRIVAT == rolle) {

			String eventMessagePrefix = this.getClass().getSimpleName() + ".authorizeUpload(...) - uploadType=" + uploadType + ": ";

			String msg = "Privatveranstalter " + benutzerID + " versucht, Datei hochzuladen";
			new LoggableEventDelegate().fireSecurityEvent(eventMessagePrefix + msg,
				domainEventHandler);
			LOGGER.warn(msg);

			throw new ActionNotAuthorizedException(applicationMessages.getString("general.actionNotAuthorized"));
		}

		Optional<Wettbewerb> optWettbewerb = this.wettbewerbService.aktuellerWettbewerb();

		if (optWettbewerb.isEmpty()) {

			String msg = null;

			switch (uploadType) {

			case KLASSENLISTE:
				msg = applicationMessages.getString("klassenimport.forbidden.nochKeineAnmeldungen");
				break;

			case AUSWERTUNG:
				msg = applicationMessages.getString("auswertungimport.forbidden.nochKeineAnmeldungen");
				break;

			default:
				break;
			}

			LOGGER.warn("Uploadversuch durch Veranstalter {}, kein Wettbewerb, UploadTyp={}", benutzerID, uploadType);
			throw new ActionNotAuthorizedException(msg);
		}

		Wettbewerb aktuellerWettbewerb = optWettbewerb.get();

		if (WettbewerbStatus.ERFASST == aktuellerWettbewerb.status()) {

			String msg = null;

			switch (uploadType) {

			case KLASSENLISTE:
				msg = applicationMessages.getString("klassenimport.forbidden.nochKeineAnmeldungen");
				break;

			case AUSWERTUNG:
				msg = applicationMessages.getString("auswertungimport.forbidden.nochKeineAnmeldungen");
				break;

			default:
				break;
			}

			LOGGER.warn("Uploadversuch durch Veranstalter {}, Wettbewerbsstatus={}, UploadTyp={}", benutzerID,
				aktuellerWettbewerb.status(), uploadType);
			throw new ActionNotAuthorizedException(msg);
		}

		if (WettbewerbStatus.BEENDET == aktuellerWettbewerb.status()) {

			String msg = null;

			switch (uploadType) {

			case KLASSENLISTE:
				msg = applicationMessages.getString("klassenimport.forbidden.wettbewerbBeendet");
				break;

			case AUSWERTUNG:
				if (!rolle.isAdmin()) {

					msg = applicationMessages.getString("auswertungimport.forbidden.wettbewerbBeendet");
				}
				break;

			default:
				break;
			}

			LOGGER.warn("Uploadversuch durch Veranstalter {}, Wettbewerbsstatus={}, UploadTyp={}", benutzerID,
				aktuellerWettbewerb.status(), uploadType);

			if (msg != null) {

				throw new ActionNotAuthorizedException(msg);
			}
		}

		Pair<Veranstalter, Teilnahme> veranstalterUndTeilnahme = this.isAuthorizedForTeilnahme(rolle, benutzerID, schulkuerzel,
			aktuellerWettbewerb, uploadType);

		if (!rolle.isAdmin()) {

			Veranstalter veranstalter = veranstalterUndTeilnahme.getLeft();

			if (UploadType.AUSWERTUNG == uploadType && WettbewerbStatus.ANMELDUNG == aktuellerWettbewerb.status()
				&& ZugangUnterlagen.ERTEILT != veranstalter.zugangUnterlagen()) {

				throw new ActionNotAuthorizedException(
					applicationMessages.getString("auswertungimport.forbidden.downloadNichtErlaubt"));
			}
		}

		List<Loesungszettel> loesungszettelAktuellerWettbewerb = loesungszettelRepository.loadAll(schulkuerzel,
			aktuellerWettbewerb.id());

		if (UploadType.AUSWERTUNG == uploadType) {

			Optional<Loesungszettel> optOnline = loesungszettelAktuellerWettbewerb.stream()
				.filter(l -> Auswertungsquelle.ONLINE == l.auswertungsquelle()).findFirst();

			if (optOnline.isPresent()) {

				LOGGER.warn(
					"{} - {} - {} versucht, Auswertungen fuer Schule {} hochzuladen, aber es gibt bereits Onlineloesungszettel",
					rolle, benutzerID, schulkuerzel);
				throw new ActionNotAuthorizedException(
					applicationMessages.getString("auswertungimport.forbidden.nurOnline"));
			}

		}

		if (UploadType.KLASSENLISTE == uploadType) {

			Optional<Loesungszettel> optUpload = loesungszettelAktuellerWettbewerb.stream()
				.filter(l -> Auswertungsquelle.UPLOAD == l.auswertungsquelle()).findFirst();

			if (optUpload.isPresent()) {

				LOGGER.warn(
					"{} - {} - {} versucht, Klassenliste fuer Schule {} hochzuladen, aber es gibt bereits Onlineloesungszettel",
					rolle, benutzerID, schulkuerzel);
				throw new ActionNotAuthorizedException(
					applicationMessages.getString("klassenimport.forbidden.hochgeladeneAuswertung"));
			}
		}

		return true;
	}

	private Pair<Veranstalter, Teilnahme> isAuthorizedForTeilnahme(final Rolle rolle, final Identifier veranstalterID, final String schulkuerzel, final Wettbewerb aktuellerWettbewerb, final UploadType uploadType) {

		String eventMessagePrefix = this.getClass().getSimpleName() + ".authorizeUpload(...) - uploadType=" + uploadType + ": ";

		Veranstalter veranstalter = null;

		if (!rolle.isAdmin()) {

			Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(veranstalterID);

			if (optVeranstalter.isEmpty()) {

				String msg = "Veranstalter mit UUID=" + veranstalterID.identifier() + " nicht gefunden";
				new LoggableEventDelegate().fireSecurityEvent(eventMessagePrefix + msg, domainEventHandler);
				LOGGER.warn(msg);

				throw new ActionNotAuthorizedException(applicationMessages.getString("general.actionNotAuthorized"));
			}

			veranstalter = optVeranstalter.get();

			if (veranstalter.zugangUnterlagen() == ZugangUnterlagen.ENTZOGEN) {

				String msg = "Lehrer " + veranstalterID + " mit entzogenem Zugang zu Unterlagen versucht, Datei hochzuladen";
				new LoggableEventDelegate().fireSecurityEvent(eventMessagePrefix + msg, domainEventHandler);
				LOGGER.warn(msg);

				throw new ActionNotAuthorizedException(applicationMessages.getString("general.actionNotAuthorized"));

			}
		}

		List<Teilnahme> teilnahmen = this.teilnahmenRepository.ofTeilnahmenummer(schulkuerzel);

		Optional<Teilnahme> optTeilnahme = teilnahmen.stream().filter(t -> t.wettbewerbID().equals(aktuellerWettbewerb.id()))
			.findFirst();

		if (optTeilnahme.isEmpty()) {

			String msg = eventMessagePrefix + "Schule mit KUERZEL="
				+ schulkuerzel + " ist nicht zum aktuellen Wettbewerb (" + aktuellerWettbewerb.id()
				+ ") angemeldet. veranstalterUUID=" + veranstalterID.toString();

			new LoggableEventDelegate().fireDataInconsistencyEvent(msg, domainEventHandler);
			LOGGER.warn(msg);

			switch (uploadType) {

			case KLASSENLISTE:
				msg = applicationMessages.getString("klassenimport.forbidden.nichtAngemeldet");
				break;

			case AUSWERTUNG:
				msg = applicationMessages.getString("auswertungimport.forbidden.nichtAngemeldet");
				break;

			default:
				break;
			}

			throw new ActionNotAuthorizedException(msg);
		}

		return Pair.of(veranstalter, optTeilnahme.get());
	}
}
