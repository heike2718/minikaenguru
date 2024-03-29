// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.pdfutils.PdfMerger;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungKlassenstufe;
import de.egladil.web.mk_gateway.domain.statistik.StatistikKlassenstufeService;
import de.egladil.web.mk_gateway.domain.statistik.pdf.SchuluebersichtPDFGenerator;
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
import de.egladil.web.mk_gateway.domain.urkunden.generator.uebersicht.AuswertungSchuluebersichtGenerator;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.CreateDatenKaengurusprungSchuleStrategie;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.CreateDatenTeilnahmeSchuleStrategie;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.FontSizeAndLines;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.SchulurkundenGenerator;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.SplitSchulnameStrategie;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KlassenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;

/**
 * SchuleUrkundenservice erstellt alle Urkunden einer gegebenen Schule sowie eine Übersichtsseite mit den Kindernamen und
 * Platzierungen.
 */
@RequestScoped
public class SchuleUrkundenservice {

	private static final Logger LOG = LoggerFactory.getLogger(SchuleUrkundenservice.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	DomainEventHandler domainEventHandler;

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
	LoggableEventDelegate eventDelegate;

	public static SchuleUrkundenservice createForIntegrationTests(final EntityManager entityManager) {

		SchuleUrkundenservice result = new SchuleUrkundenservice();
		result.loesungezettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);
		result.kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		result.authService = AuthorizationService.createForIntegrationTest(entityManager);
		result.teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(entityManager);
		result.klassenRepository = KlassenHibernateRepository.createForIntegrationTest(entityManager);
		result.urkundenmotivRepository = new UrkundenmotivRepository();
		result.wettbewerbService = WettbewerbService.createForIntegrationTest(entityManager);
		result.domainEventHandler = DomainEventHandler.createForIntegrationTest(entityManager);
		return result;
	}

	public DownloadData generiereSchulauswertung(final UrkundenauftragSchule urkundenauftrag, final Identifier veranstalterID) {

		String schulkuerzel = urkundenauftrag.schulkuerzel();

		this.authService.checkPermissionForTeilnahmenummerAndReturnRolle(veranstalterID, new Identifier(schulkuerzel),
			"[generiereSchulauswertung - schulkuerzel=" + schulkuerzel + "]");

		TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifierAktuellerWettbewerb = TeilnahmeIdentifierAktuellerWettbewerb
			.createForSchulteilnahme(schulkuerzel);

		TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier
			.createFromTeilnahmeIdentifierAktuellesJahr(teilnahmeIdentifierAktuellerWettbewerb)
			.withWettbewerbID(getWettbewerb().id());

		Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier);

		if (optTeilnahme.isEmpty()) {

			throw new NotFoundException("Schule ist nicht zum Wettbewerb " + getWettbewerb().id().jahr() + " angemeldet");

		}

		Schulteilnahme schulteilnahme = (Schulteilnahme) optTeilnahme.get();

		List<Loesungszettel> alleLoesungszettel = loesungezettelRepository.loadAll(teilnahmeIdentifier);

		if (alleLoesungszettel.isEmpty()) {

			throw new NotFoundException(
				"Es gibt (noch) keine Lösungszettel für den Wettbewerb " + getWettbewerb().id().jahr());

		}

		AuswertungDatenRepository datenRepository = this.bereiteDatenVor(schulteilnahme, urkundenauftrag, alleLoesungszettel);

		List<byte[]> seiten = new ArrayList<>();

		try {

			seiten.add(new AuswertungSchuluebersichtGenerator().generiereSchuluebersicht(schulteilnahme, datenRepository));

			SchuluebersichtPDFGenerator statistikGenerator = new SchuluebersichtPDFGenerator();
			List<byte[]> statistikseiten = statistikGenerator.generiereStatistikseiten(datenRepository.getGesamtpunktverteilungen(),
				true);

			seiten.addAll(statistikseiten);

			SchulurkundenGenerator urkundenGenerator = new SchulurkundenGenerator();
			seiten.addAll(urkundenGenerator.generiereUrkunden(datenRepository));

			PdfMerger pdfMerger = new PdfMerger();

			// FIXME: I0407: Dieser Teil kann wieder raus, sobald wir mehr RAM haben. Bleibt nur noch der else-Teil übrig.
			if (seiten.size() > 100) {

				byte[] datenBunch1 = null;
				byte[] datenBunch2 = null;
				List<byte[]> bunch = new ArrayList<>();

				for (int i = 0; i < 100; i++) {

					bunch.add(seiten.get(i));
				}

				datenBunch1 = pdfMerger.concatPdf(bunch);
				bunch = new ArrayList<>();

				for (int i = 100; i < seiten.size(); i++) {

					bunch.add(seiten.get(i));

				}

				datenBunch2 = pdfMerger.concatPdf(bunch);

				List<byte[]> bunches = new ArrayList<>();
				bunches.add(datenBunch1);
				bunches.add(datenBunch2);

				byte[] daten = pdfMerger.concatPdf(bunches);
				String dateiname = this.getDateiname(schulteilnahme);

				return new DownloadData(dateiname, daten);

			} else {

				byte[] daten = pdfMerger.concatPdf(seiten);
				String dateiname = this.getDateiname(schulteilnahme);

				return new DownloadData(dateiname, daten);
			}

		} finally {

			// Memory-Leak
			seiten.clear();
			seiten = null;
		}
	}

	/**
	 * Die Daten der Schule werden gesammelt und für verschiedene Zwecke strukturiert aufbereitet. Die Methode ist public, damit sie
	 * außerhalb des Artefacts getestet werden kann.
	 *
	 * @param  urkundenauftrag
	 * @return
	 */
	public AuswertungDatenRepository bereiteDatenVor(final Schulteilnahme schulteilnahme, final UrkundenauftragSchule urkundenauftrag, final List<Loesungszettel> alleLoesungszettel) {

		TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifierAktuellerWettbewerb = TeilnahmeIdentifierAktuellerWettbewerb
			.createForSchulteilnahme(schulteilnahme.teilnahmenummer().identifier());

		List<Kind> kinder = kinderRepository.withTeilnahmeHavingLoesungszettel(teilnahmeIdentifierAktuellerWettbewerb);

		List<Klasse> klassen = klassenRepository.findKlassenWithSchule(schulteilnahme.teilnahmenummer());

		FontSizeAndLines fontSizeAndLinesSchulname = new SplitSchulnameStrategie()
			.getFontSizeAndLines(schulteilnahme.nameSchule());

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
				eventDelegate.fireDataInconsistencyEvent(msg, domainEventHandler);
			}

		}

		Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> statistikMap = new HashMap<>();

		StatistikKlassenstufeService statistikService = new StatistikKlassenstufeService();
		WettbewerbID wettbewerbID = getWettbewerb().id();

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			List<Loesungszettel> loesungszettelKlassenstufe = alleLoesungszettel.stream()
				.filter(l -> klassenstufe == l.klassenstufe()).collect(Collectors.toList());

			if (loesungszettelKlassenstufe.size() > 0) {

				GesamtpunktverteilungKlassenstufe verteilung = statistikService.generiereGesamtpunktverteilung(wettbewerbID,
					klassenstufe, loesungszettelKlassenstufe);

				statistikMap.put(klassenstufe, verteilung);
			}
		}

		return this.createTheDatenrepository(datenTeilnahmeurkunden, datenKaengurusprungurkunden, alleDatenUebersicht,
			statistikMap);
	}

	private String getDateiname(final Schulteilnahme schulteilnahme) {

		String dateiname = MessageFormat.format(applicationMessages.getString("schulauswertung.pdf.dateiname.schule"),
			new Object[] { getWettbewerb().id().toString(), schulteilnahme.getTransformedNameForDownloads() });

		return dateiname;
	}

	private AuswertungDatenRepository createTheDatenrepository(final List<AbstractDatenUrkunde> datenTeilnahmeurkunden, final List<AbstractDatenUrkunde> datenKaengurusprungurkunden, final List<KinddatenUebersicht> alleDatenUebersicht, final Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> gesamtpunktverteilungen) {

		Map<Klassenstufe, List<KinddatenUebersicht>> uebersichtMap = new KinddatenUebersichtKlassenstufeMapper()
			.apply(alleDatenUebersicht);

		return new AuswertungDatenRepository(datenTeilnahmeurkunden, datenKaengurusprungurkunden, uebersichtMap,
			gesamtpunktverteilungen);
	}

	private Wettbewerb getWettbewerb() {

		return this.wettbewerbService.aktuellerWettbewerb().get();
	}

}
