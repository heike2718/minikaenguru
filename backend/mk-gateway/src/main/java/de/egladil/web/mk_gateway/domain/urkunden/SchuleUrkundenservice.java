// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.urkunden.api.UrkundenauftragSchule;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AuswertungDatenRepository;
import de.egladil.web.mk_gateway.domain.urkunden.daten.KinddatenUebersicht;
import de.egladil.web.mk_gateway.domain.urkunden.daten.KinddatenUebersichtKlassenstufeMapper;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.CreateDatenKaengurusprungSchuleStrategie;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.CreateDatenTeilnahmeSchuleStrategie;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.FontSizeAndLines;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.SplitSchulnameStrategie;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KlassenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.WettbewerbHibernateRepository;

/**
 * SchuleUrkundenservice erstellt alle Urkunden einer gegebenen Schule sowie eine Übersichtsseite mit den Kindernamen und
 * Platzierungen.
 */
@ApplicationScoped
public class SchuleUrkundenservice {

	private static final Logger LOG = LoggerFactory.getLogger(SchuleUrkundenservice.class);

	@Inject
	LoesungszettelRepository loesungezettelRepository;

	@Inject
	KinderRepository kinderRepository;

	@Inject
	AuthorizationService authService;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	KlassenRepository klassenRepository;

	@Inject
	UrkundenmotivRepository urkundenmotivRepository;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	private Wettbewerb aktuellerWettbewerb;

	public static SchuleUrkundenservice createForIntegrationTests(final EntityManager entityManager) {

		SchuleUrkundenservice result = new SchuleUrkundenservice();
		result.loesungezettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);
		result.kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		result.authService = AuthorizationService.createForIntegrationTests(entityManager);
		result.teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(entityManager);
		result.klassenRepository = KlassenHibernateRepository.createForIntegrationTest(entityManager);
		result.urkundenmotivRepository = new UrkundenmotivRepository();
		result.wettbewerbService = WettbewerbService
			.createForTest(WettbewerbHibernateRepository.createForIntegrationTest(entityManager));

		return result;
	}

	public DownloadData generiereSchulauswertung(final UrkundenauftragSchule urkundenauftrag, final Identifier veranstalterID) {

		String schulkuerzel = urkundenauftrag.schulkuerzel();

		this.authService.checkPermissionForTeilnahmenummer(veranstalterID, new Identifier(schulkuerzel),
			"[generiereSchulauswertung - schulkuerzel=" + schulkuerzel + "]");

		AuswertungDatenRepository datenRepository = this.bereiteDatenVor(urkundenauftrag);

		return null;
	}

	/**
	 * Die Daten der Schule werden gesammelt und für verschiedene Zwecke strukturiert aufbereitet. Die Methode ist public, damit sie
	 * außerhalb des Artefacts getestet werden kann.
	 *
	 * @param  urkundenauftrag
	 * @return
	 */
	public AuswertungDatenRepository bereiteDatenVor(final UrkundenauftragSchule urkundenauftrag) {

		String schulkuerzel = urkundenauftrag.schulkuerzel();

		TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifierAktuellerWettbewerb = TeilnahmeIdentifierAktuellerWettbewerb
			.createForSchulteilnahme(schulkuerzel);

		TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier
			.createFromTeilnahmeIdentifierAktuellesJahr(teilnahmeIdentifierAktuellerWettbewerb)
			.withWettbewerbID(getWettbewerb().id());

		Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier);

		if (optTeilnahme.isEmpty()) {

			throw new NotFoundException("Schule ist nicht zum Wettbewerb " + getWettbewerb().id().jahr() + " angemeldet");

		}

		List<Kind> kinder = kinderRepository.withTeilnahmeHavingLoesungszettel(teilnahmeIdentifierAktuellerWettbewerb);

		List<Loesungszettel> alleLoesungszettel = loesungezettelRepository.loadAll(teilnahmeIdentifier);

		if (kinder.isEmpty()) {

			throw new NotFoundException(
				"Es gibt keine Lösungszettel für den Wettbewerb " + getWettbewerb().id().jahr() + " angemeldet");

		}

		Schulteilnahme teilnahme = (Schulteilnahme) optTeilnahme.get();

		List<Klasse> klassen = klassenRepository.findKlassenWithSchule(new Identifier(schulkuerzel));

		FontSizeAndLines fontSizeAndLinesSchulname = new SplitSchulnameStrategie()
			.getFontSizeAndLines(teilnahme.nameSchule());

		Urkundenmotiv urkundenmotiv = urkundenmotivRepository.findByFarbschema(urkundenauftrag.farbschema());

		String datumDeutsch = urkundenauftrag.dateString();
		String datumEnglisch = datumDeutsch.replaceAll("\\.", "/");

		List<AbstractDatenUrkunde> datenTeilnahmeurkunden = new ArrayList<>();
		List<AbstractDatenUrkunde> datenKaengurusprungurkunden = new ArrayList<>();
		List<KinddatenUebersicht> alleDatenUebersicht = new ArrayList<>();

		for (Kind kind : kinder) {

			Klasse klasse = klassen.stream().filter(kl -> kl.identifier().equals(kind.klasseID())).findAny().get();

			Optional<Loesungszettel> optLoesungszettel = alleLoesungszettel.stream()
				.filter(l -> l.identifier().equals(kind.loesungszettelID())).findFirst();

			if (optLoesungszettel.isPresent()) {

				Loesungszettel loesungszettel = optLoesungszettel.get();

				String datum = kind.sprache() == Sprache.de ? datumDeutsch : datumEnglisch;

				AbstractDatenUrkunde datenTeilnahmeurkunde = new CreateDatenTeilnahmeSchuleStrategie(fontSizeAndLinesSchulname,
					klasse).createDatenUrkunde(kind,
						optLoesungszettel.get(), datum, urkundenmotiv);

				datenTeilnahmeurkunden.add(datenTeilnahmeurkunde);

				AbstractDatenUrkunde datenKaengurusprungurkunde = new CreateDatenKaengurusprungSchuleStrategie(
					fontSizeAndLinesSchulname,
					klasse).createDatenUrkunde(kind,
						optLoesungszettel.get(), datum, urkundenmotiv);

				datenKaengurusprungurkunden.add(datenKaengurusprungurkunde);

				KinddatenUebersicht datenUebersicht = new KinddatenUebersicht()
					.withUuid(kind.identifier().identifier())
					.withFullName(kind.nameUrkunde())
					.withKlassenstufe(kind.klassenstufe())
					.withNameKlasse(klasse.name())
					.withLaengeKaengurusprung(loesungszettel.laengeKaengurusprung())
					.withPunkte(loesungszettel.punkte());

				alleDatenUebersicht.add(datenUebersicht);
			} else {

				String msg = "generiereSchulauswertung: Loesungszettel zu Kind wurde nicht gefunden: kindUUID="
					+ kind.identifier().identifier() + " - Kind wird weggelassen";
				LOG.warn(msg);
				new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);
			}

		}

		return this.createTheDatenrepository(datenTeilnahmeurkunden, datenKaengurusprungurkunden, alleDatenUebersicht);
	}

	private AuswertungDatenRepository createTheDatenrepository(final List<AbstractDatenUrkunde> datenTeilnahmeurkunden, final List<AbstractDatenUrkunde> datenKaengurusprungurkunden, final List<KinddatenUebersicht> alleDatenUebersicht) {

		Map<Klassenstufe, List<KinddatenUebersicht>> uebersichtMap = new KinddatenUebersichtKlassenstufeMapper()
			.apply(alleDatenUebersicht);

		return new AuswertungDatenRepository(datenTeilnahmeurkunden, datenKaengurusprungurkunden, uebersichtMap);
	}

	private Wettbewerb getWettbewerb() {

		if (aktuellerWettbewerb == null) {

			this.aktuellerWettbewerb = this.wettbewerbService.aktuellerWettbewerb().get();
		}

		return aktuellerWettbewerb;
	}

}
