// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.uploads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.klassenlisten.api.KlassenlisteImportReport;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.AuswertungImportReport;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.UploadAuswertungContext;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.uploads.UploadData;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.uploads.impl.UploadManagerImpl;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.UploadsMonitoringViewItem;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UploadHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * UploadManagerImplIT
 */
public class UploadManagerImplIT extends AbstractIntegrationTest {

	private UploadManagerImpl uploadManager;

	private KinderRepository kinderRepository;

	private UploadRepository uploadRepository;

	private LoesungszettelRepository loesungszettelRepository;

	private Wettbewerb wettbewerb;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		uploadManager = UploadManagerImpl.createForIntegrationTests(entityManager);
		kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		uploadRepository = UploadHibernateRepository.createForIntegrationTests(entityManager);
		loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);

		wettbewerb = new Wettbewerb(new WettbewerbID(2020)).withLoesungsbuchstabenIKids("AA-CB-BC")
			.withLoesungsbuchstabenKlasse1("EBCA-CCDB-EBAD").withLoesungsbuchstabenKlasse2("DBCEA-ABCED-BCBEB");
		WettbewerbStatus status = wettbewerb.status();

		while (WettbewerbStatus.DOWNLOAD_PRIVAT != status) {

			wettbewerb.naechsterStatus();
			status = wettbewerb.status();
		}

	}

	@Nested
	class ImportKlassenlisteTests {

		@Test
		void should_uploadKlassenliste_work() {

			// Arrange
			String benutzerUuid = "2f09da36-07c6-4033-a2f1-5e110c804026";
			String schulkuerzel = "2EX6DENW";
			TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier = TeilnahmeIdentifierAktuellerWettbewerb
				.createForSchulteilnahme(schulkuerzel);

			byte[] data = loadData("/klassenlisten/klassenliste.csv");
			UploadData uploadData = new UploadData("Bilinguale_Grundschule_Altmark.xlsx", data);

			UploadKlassenlisteContext contextObject = new UploadKlassenlisteContext().withKuerzelLand("DE-ST")
				.withNachnameAlsZusatz(false).withSprache(Sprache.de).withWettbewerb(wettbewerb);

			UploadRequestPayload uploadRequestPayload = new UploadRequestPayload().withContext(contextObject)
				.withUploadData(uploadData)
				.withTeilnahmenummer(schulkuerzel).withUploadType(UploadType.KLASSENLISTE)
				.withBenutzerID(new Identifier(benutzerUuid));

			// Act
			EntityTransaction transaction = startTransaction();
			ResponsePayload result = uploadManager.processUpload(uploadRequestPayload);
			commit(transaction);

			// Assert
			MessagePayload messagePayload = result.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Einige Kinder konnten nicht importiert werden. Den Fehlerreport können Sie herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert. Bitte prüfen Sie außerdem, ob Umlaute korrekt angezeigt werden.",
				messagePayload.getMessage());

			KlassenlisteImportReport importReport = (KlassenlisteImportReport) result.getData();
			assertEquals(Long.valueOf(1L), Long.valueOf(importReport.getAnzahlDubletten()));
			assertEquals(10, importReport.getAnzahlKinderImportiert());
			assertEquals(3, importReport.getAnzahlKlassen());
			assertEquals(Long.valueOf(2L), importReport.getAnzahlKlassenstufeUnklar());
			assertEquals(1, importReport.getAnzahlNichtImportiert());
			List<KlasseAPIModel> klassen = importReport.getKlassen();

			assertEquals(3, klassen.size());

			{

				Optional<KlasseAPIModel> optKlasse = klassen.stream().filter(k -> "1a".equals(k.name())).findAny();
				assertTrue(optKlasse.isPresent());
				KlasseAPIModel klasse = optKlasse.get();
				assertEquals(3, klasse.anzahlKinder());
				assertEquals(schulkuerzel, klasse.schulkuerzel());
				assertEquals(0, klasse.anzahlLoesungszettel());
			}

			{

				Optional<KlasseAPIModel> optKlasse = klassen.stream().filter(k -> "2a".equals(k.name())).findAny();
				assertTrue(optKlasse.isPresent());
				KlasseAPIModel klasse = optKlasse.get();
				assertEquals(3, klasse.anzahlKinder());
				assertEquals(schulkuerzel, klasse.schulkuerzel());
				assertEquals(0, klasse.anzahlLoesungszettel());

			}

			{

				Optional<KlasseAPIModel> optKlasse = klassen.stream().filter(k -> "2b".equals(k.name())).findAny();
				assertTrue(optKlasse.isPresent());
				KlasseAPIModel klasse = optKlasse.get();
				assertEquals(4, klasse.anzahlKinder());
				assertEquals(schulkuerzel, klasse.schulkuerzel());
				assertEquals(0, klasse.anzahlLoesungszettel());

			}

			List<Kind> kinder = kinderRepository.withTeilnahme(teilnahmeIdentifier);
			assertEquals(10, kinder.size());

			List<String> fehlerUndWarnungen = importReport.getFehlerUndWarnungen();

			System.out.println(fehlerUndWarnungen);

			assertEquals(4, fehlerUndWarnungen.size());
			assertEquals(
				"Zeile 8: Fehler! \"2a;Heinz;2\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen. Es sind weniger als 4 Angaben.",
				fehlerUndWarnungen.get(0));

			assertEquals(
				"Zeile 3: Katja;Fassbinder;1a;4: diese Klassenstufe gibt es nicht. Die Klassenstufe wurde auf \"2\" gesetzt.",
				fehlerUndWarnungen.get(1));

			assertEquals(
				"Zeile 9: Amiera;Kaled;2a;2: In Klasse 2a gibt es bereits ein Kind mit diesem Namen und dieser Klassenstufe",
				fehlerUndWarnungen.get(2));

			assertEquals("Zeile 10: Annalena;Log;2b;3: diese Klassenstufe gibt es nicht. Die Klassenstufe wurde auf \"2\" gesetzt.",
				fehlerUndWarnungen.get(3));

			List<UploadsMonitoringViewItem> uploads = uploadRepository.findUploadsWithUploadTypeAndTeilnahmenummer(
				UploadType.KLASSENLISTE,
				schulkuerzel);

			assertEquals(1, uploads.size());

			UploadsMonitoringViewItem persistenterUpload = uploads.get(0);
			assertEquals(UploadStatus.DATENFEHLER, persistenterUpload.getStatus());

			String path = "/home/heike/git/testdaten/minikaenguru/integrationtests/upload/" + persistenterUpload.getUuid()
				+ "-fehlerreport.csv";

			File fehlerfile = new File(path);
			assertTrue(fehlerfile.exists());
			assertTrue(fehlerfile.isFile());
			assertTrue(fehlerfile.canRead());
		}

		@Test
		void should_uploadKlassenlisteDetectDubletten() {

			// Arrange
			String benutzerUuid = "a6bf38f2-5450-4720-9688-9c239a2e87c8";
			String schulkuerzel = "EEF8FOYK";
			TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier = TeilnahmeIdentifierAktuellerWettbewerb
				.createForSchulteilnahme(schulkuerzel);

			byte[] data = loadData("/klassenlisten/klassenliste-EEF8FOYK.csv");
			UploadData uploadData = new UploadData("Irgendein Schulname.csv", data);

			UploadKlassenlisteContext contextObject = new UploadKlassenlisteContext().withKuerzelLand("DE-HB")
				.withNachnameAlsZusatz(false).withSprache(Sprache.de).withWettbewerb(wettbewerb);

			UploadRequestPayload uploadRequestPayload = new UploadRequestPayload().withContext(contextObject)
				.withUploadData(uploadData)
				.withTeilnahmenummer(schulkuerzel).withUploadType(UploadType.KLASSENLISTE)
				.withBenutzerID(new Identifier(benutzerUuid));

			// Act
			EntityTransaction transaction = startTransaction();
			ResponsePayload result = uploadManager.processUpload(uploadRequestPayload);
			commit(transaction);

			// Assert
			MessagePayload messagePayload = result.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Es gab möglicherweise Doppeleinträge. Alle betroffenen Kinder wurden markiert. Bitte prüfen Sie außerdem, ob Umlaute korrekt angezeigt werden.",
				messagePayload.getMessage());

			KlassenlisteImportReport importReport = (KlassenlisteImportReport) result.getData();
			assertEquals(Long.valueOf(1L), Long.valueOf(importReport.getAnzahlDubletten()));
			assertEquals(2, importReport.getAnzahlKinderImportiert());
			assertEquals(1, importReport.getAnzahlKlassen());
			assertEquals(Long.valueOf(0), importReport.getAnzahlKlassenstufeUnklar());
			assertEquals(0, importReport.getAnzahlNichtImportiert());
			List<KlasseAPIModel> klassen = importReport.getKlassen();

			assertEquals(1, klassen.size());

			List<UploadsMonitoringViewItem> uploads = uploadRepository.findUploadsWithUploadTypeAndTeilnahmenummer(
				UploadType.KLASSENLISTE,
				schulkuerzel);

			assertEquals(1, uploads.size());

			List<Kind> kinder = kinderRepository.withTeilnahme(teilnahmeIdentifier);
			assertEquals(4, kinder.size());

			List<Kind> dubletten = kinder.stream().filter(k -> "Mirkovitz".equals(k.nachname())).collect(Collectors.toList());
			assertEquals(2, dubletten.size());

			int anzahlMitDublettePruefen = 0;
			int anzahlImportiert = 0;

			{

				Kind kind = dubletten.get(0);
				assertFalse(kind.equals(dubletten.get(1)));

				if (kind.isImportiert()) {

					anzahlImportiert++;
				}

				if (kind.isDublettePruefen()) {

					anzahlMitDublettePruefen++;
				}
			}

			{

				Kind kind = dubletten.get(1);
				assertFalse(kind.equals(dubletten.get(0)));

				if (kind.isImportiert()) {

					anzahlImportiert++;
				}

				if (kind.isDublettePruefen()) {

					anzahlMitDublettePruefen++;
				}
			}

			assertEquals(1, anzahlMitDublettePruefen);
			assertEquals(1, anzahlImportiert);

			UploadsMonitoringViewItem persistenterUpload = uploads.get(0);
			assertEquals(UploadStatus.DATENFEHLER, persistenterUpload.getStatus());

			String path = "/home/heike/git/testdaten/minikaenguru/integrationtests/upload/" + persistenterUpload.getUuid()
				+ "-fehlerreport.csv";

			File fehlerfile = new File(path);
			assertTrue(fehlerfile.exists());
		}

		@Test
		void should_uploadKlassenlisteDetectFehler() {

			// Arrange
			String benutzerUuid = "41ede553-a5ba-4167-bfa6-fea0faacf8d7";
			String schulkuerzel = "R31VPEJH";
			TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier = TeilnahmeIdentifierAktuellerWettbewerb
				.createForSchulteilnahme(schulkuerzel);

			List<UploadsMonitoringViewItem> uploadsVorher = uploadRepository
				.findUploadsWithUploadTypeAndTeilnahmenummer(UploadType.KLASSENLISTE, schulkuerzel);

			byte[] data = loadData("/klassenlisten/klassenliste-M5ZD2NL2-mit-fehlern.csv");
			UploadData uploadData = new UploadData("Irgendein Schulname.csv", data);

			UploadKlassenlisteContext contextObject = new UploadKlassenlisteContext().withKuerzelLand("DE-HB")
				.withNachnameAlsZusatz(false).withSprache(Sprache.de).withWettbewerb(wettbewerb);

			UploadRequestPayload uploadRequestPayload = new UploadRequestPayload().withContext(contextObject)
				.withUploadData(uploadData)
				.withTeilnahmenummer(schulkuerzel).withUploadType(UploadType.KLASSENLISTE)
				.withBenutzerID(new Identifier(benutzerUuid));

			// Act
			EntityTransaction transaction = startTransaction();
			ResponsePayload result = uploadManager.processUpload(uploadRequestPayload);
			commit(transaction);

			// Assert
			MessagePayload messagePayload = result.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Einige Kinder konnten nicht importiert werden. Den Fehlerreport können Sie herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert. Bitte prüfen Sie außerdem, ob Umlaute korrekt angezeigt werden.",
				messagePayload.getMessage());

			KlassenlisteImportReport importReport = (KlassenlisteImportReport) result.getData();

			List<String> fehlerUndWarnungen = importReport.getFehlerUndWarnungen();
			fehlerUndWarnungen.stream().forEach(m -> System.out.println(m));

			assertEquals(2, importReport.getAnzahlKlassen());
			assertEquals(6, importReport.getAnzahlKinderImportiert());
			assertEquals(Long.valueOf(1), importReport.getAnzahlKlassenstufeUnklar());
			assertEquals(1, importReport.getAnzahlNichtImportiert());
			assertEquals(Long.valueOf(1L), Long.valueOf(importReport.getAnzahlDubletten()));

			assertEquals(3, fehlerUndWarnungen.size());
			assertEquals(
				"Zeile 3: Fehler! \"Malte;Fischer\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen. Es sind weniger als 4 Angaben.",
				fehlerUndWarnungen.get(0));
			assertEquals("Zeile 1: Heide;Witzka;2a;3: diese Klassenstufe gibt es nicht. Die Klassenstufe wurde auf \"2\" gesetzt.",
				fehlerUndWarnungen.get(1));
			assertEquals(
				"Zeile 5: Heide;Witzka;2a;2: In Klasse 2a gibt es bereits ein Kind mit diesem Namen und dieser Klassenstufe",
				fehlerUndWarnungen.get(2));

			List<KlasseAPIModel> klassen = importReport.getKlassen();

			assertEquals(2, klassen.size());

			List<Kind> kinder = kinderRepository.withTeilnahme(teilnahmeIdentifier);
			assertEquals(6, kinder.size());

			List<Kind> importierte = kinder.stream().filter(k -> k.isImportiert()).collect(Collectors.toList());
			assertEquals(6, importierte.size());

			List<Kind> dubletten = kinder.stream().filter(k -> k.isDublettePruefen()).collect(Collectors.toList());
			assertEquals(1, dubletten.size());

			List<UploadsMonitoringViewItem> uploads = uploadRepository.findUploadsWithUploadTypeAndTeilnahmenummer(
				UploadType.KLASSENLISTE,
				schulkuerzel);

			assertEquals(uploadsVorher.size() + 1, uploads.size());

			Optional<UploadsMonitoringViewItem> optNeuer = uploads.stream().filter(u -> !uploadsVorher.contains(u)).findFirst();

			UploadsMonitoringViewItem persistenterUpload = optNeuer.get();
			assertEquals(UploadStatus.DATENFEHLER, persistenterUpload.getStatus());

			String path = "/home/heike/git/testdaten/minikaenguru/integrationtests/upload/" + persistenterUpload.getUuid()
				+ "-fehlerreport.csv";

			File fehlerfile = new File(path);
			assertTrue(fehlerfile.exists());
		}
	}

	@Nested
	class ImportAuswertungTests {

		@Test
		void should_uploadAuswertungByAdmin_work_whenCsv() {

			// Arrange
			String benutzerUuid = "it-db-inside-docker";
			String schulkuerzel = "UUBW0AZW";

			byte[] data = loadData("/auswertungen/auswertung-UUBW0AZW.csv");
			UploadData uploadData = new UploadData("Auswertung-Grundschule Börgitz \"Hans Beimler\".xslx", data);

			UploadAuswertungContext contextObject = new UploadAuswertungContext().withWettbewerb(wettbewerb)
				.withKuerzelLand("DE-ST")
				.withSprache(Sprache.en);

			UploadRequestPayload uploadRequestPayload = new UploadRequestPayload().withContext(contextObject)
				.withUploadData(uploadData)
				.withTeilnahmenummer(schulkuerzel).withUploadType(UploadType.AUSWERTUNG)
				.withBenutzerID(new Identifier(benutzerUuid));

			// Act
			EntityTransaction transaction = startTransaction();
			ResponsePayload result = uploadManager.processUpload(uploadRequestPayload);
			commit(transaction);

			// Assert
			MessagePayload messagePayload = result.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals("Die Auswertung wurde erfolgreich importiert. Vielen Dank!", messagePayload.getMessage());

			AuswertungImportReport report = (AuswertungImportReport) result.getData();
			assertTrue(report.getFehlerhafteZeilen().isEmpty());

			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);

			assertEquals(24, teilnahme.anzahlKinder());
			assertEquals(24, teilnahme.getAnzahlLoesungszettelUpload());
			assertEquals(0, teilnahme.getAnzahlLoesungszettelOnline());
			TeilnahmeIdentifier teilnahmeIdentifier = teilnahme.identifier();
			assertEquals(2020, teilnahmeIdentifier.jahr());
			assertEquals(schulkuerzel, teilnahmeIdentifier.teilnahmenummer());
			assertEquals(Teilnahmeart.SCHULE, teilnahmeIdentifier.teilnahmeart());

			List<Loesungszettel> alleLoesungszettel = loesungszettelRepository.loadAll(teilnahmeIdentifier);

			for (Loesungszettel loesungszettel : alleLoesungszettel) {

				assertEquals(Auswertungsquelle.UPLOAD, loesungszettel.auswertungsquelle());
				assertEquals("DE-ST", loesungszettel.landkuerzel());
				assertEquals(Klassenstufe.ZWEI, loesungszettel.klassenstufe());
				assertEquals(Sprache.en, loesungszettel.sprache());
				LoesungszettelRohdaten rohdaten = loesungszettel.rohdaten();
				assertFalse(rohdaten.hatTypo());
				assertEquals(rohdaten.nutzereingabe(), rohdaten.wertungscode());
				assertNull(rohdaten.antwortcode());

			}

		}

		@Test
		void should_uploadAuswertungByAdmin_rejectUpload_when_AuswertungOnline() {

			// Arrange
			String benutzerUuid = "it-db-inside-docker";
			String schulkuerzel = "M5ZD2NL2";

			byte[] data = loadData("/auswertungen/auswertung-GSJIS9J8.xlsx");
			UploadData uploadData = new UploadData("Auswertung-Rosental.xslx", data);

			UploadAuswertungContext contextObject = new UploadAuswertungContext().withWettbewerb(wettbewerb)
				.withKuerzelLand("DE-ST")
				.withSprache(Sprache.de);

			UploadRequestPayload uploadRequestPayload = new UploadRequestPayload().withContext(contextObject)
				.withUploadData(uploadData)
				.withTeilnahmenummer(schulkuerzel).withUploadType(UploadType.AUSWERTUNG)
				.withBenutzerID(new Identifier(benutzerUuid));

			// Act
			EntityTransaction transaction = startTransaction();
			ResponsePayload result = uploadManager.processUpload(uploadRequestPayload);
			commit(transaction);

			// Assert
			MessagePayload messagePayload = result.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals(
				"Der Wettbewerb an dieser Schule wurde bereits online ausgewertet. Die Auswertungstabelle wird ignoriert.",
				messagePayload.getMessage());

			AuswertungImportReport report = (AuswertungImportReport) result.getData();
			assertNull(report);
		}

		@Test
		void should_uploadAuswertungByAdmin_returnWarn_whenNamenspalteAberKeineNamen() {

			// Arrange
			String benutzerUuid = "it-db-inside-docker";
			String schulkuerzel = "GSJIS9J8";

			byte[] data = loadData("/auswertungen/auswertung-GSJIS9J8.xlsx");
			UploadData uploadData = new UploadData("Auswertung-Grundschule-Geusa.xslx", data);

			UploadAuswertungContext contextObject = new UploadAuswertungContext().withWettbewerb(wettbewerb)
				.withKuerzelLand("DE-ST")
				.withSprache(Sprache.de);

			UploadRequestPayload uploadRequestPayload = new UploadRequestPayload().withContext(contextObject)
				.withUploadData(uploadData)
				.withTeilnahmenummer(schulkuerzel).withUploadType(UploadType.AUSWERTUNG)
				.withBenutzerID(new Identifier(benutzerUuid));

			// Act
			EntityTransaction transaction = startTransaction();
			ResponsePayload result = uploadManager.processUpload(uploadRequestPayload);
			commit(transaction);

			// Assert
			MessagePayload messagePayload = result.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich hochgeladen. Sie muss noch nachbearbeitet werden. Die Statistik steht Ihnen in einigen Tagen zur Verfügung.",
				messagePayload.getMessage());

			AuswertungImportReport report = (AuswertungImportReport) result.getData();
			assertTrue(report.getFehlerhafteZeilen().isEmpty());

			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);

			assertEquals(0, teilnahme.anzahlKinder());
			assertEquals(0, teilnahme.getAnzahlLoesungszettelUpload());
			assertEquals(0, teilnahme.getAnzahlLoesungszettelOnline());
			TeilnahmeIdentifier teilnahmeIdentifier = teilnahme.identifier();
			assertEquals(2020, teilnahmeIdentifier.jahr());
			assertEquals(schulkuerzel, teilnahmeIdentifier.teilnahmenummer());
			assertEquals(Teilnahmeart.SCHULE, teilnahmeIdentifier.teilnahmeart());
		}

		@Test
		void should_uploadAuswertungByAdmin_work_whenDateiMitEchtdatenKlasse1() {

			// Arrange
			String benutzerUuid = "it-db-inside-docker";
			String schulkuerzel = "U9OI773A";

			byte[] data = loadData("/auswertungen/2021_auswertung_minikaenguru_klasse_1.xlsx");
			UploadData uploadData = new UploadData("Auswertung-Evangelische Grundschule Diakonie-Klasse 1.xslx", data);

			UploadAuswertungContext contextObject = new UploadAuswertungContext().withWettbewerb(wettbewerb)
				.withKuerzelLand("DE-ST")
				.withSprache(Sprache.de);

			UploadRequestPayload uploadRequestPayload = new UploadRequestPayload().withContext(contextObject)
				.withUploadData(uploadData)
				.withTeilnahmenummer(schulkuerzel).withUploadType(UploadType.AUSWERTUNG)
				.withBenutzerID(new Identifier(benutzerUuid));

			// Act
			EntityTransaction transaction = startTransaction();
			ResponsePayload result = uploadManager.processUpload(uploadRequestPayload);
			commit(transaction);

			// Assert
			MessagePayload messagePayload = result.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals("Die Auswertung wurde erfolgreich importiert. Vielen Dank!", messagePayload.getMessage());

			AuswertungImportReport report = (AuswertungImportReport) result.getData();
			assertTrue(report.getFehlerhafteZeilen().isEmpty());

			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);

			assertEquals(40, teilnahme.anzahlKinder());
			assertEquals(40, teilnahme.getAnzahlLoesungszettelUpload());
			assertEquals(0, teilnahme.getAnzahlLoesungszettelOnline());
			TeilnahmeIdentifier teilnahmeIdentifier = teilnahme.identifier();
			assertEquals(2020, teilnahmeIdentifier.jahr());
			assertEquals(schulkuerzel, teilnahmeIdentifier.teilnahmenummer());
			assertEquals(Teilnahmeart.SCHULE, teilnahmeIdentifier.teilnahmeart());

			List<Loesungszettel> alleLoesungszettel = loesungszettelRepository.loadAll(teilnahmeIdentifier);

			for (Loesungszettel loesungszettel : alleLoesungszettel) {

				assertEquals(Auswertungsquelle.UPLOAD, loesungszettel.auswertungsquelle());
				assertEquals("DE-ST", loesungszettel.landkuerzel());
				assertEquals(Klassenstufe.EINS, loesungszettel.klassenstufe());
				assertEquals(Sprache.de, loesungszettel.sprache());
				LoesungszettelRohdaten rohdaten = loesungszettel.rohdaten();
				assertFalse(rohdaten.hatTypo());
				assertNull(rohdaten.antwortcode());

				System.out.println(rohdaten.nutzereingabe());

				char[] nutzereingabeTokens = rohdaten.nutzereingabe().toCharArray();
				assertEquals(12, nutzereingabeTokens.length, "Name wurde nicht entfernt bei " + loesungszettel.identifier());
			}
		}
	}

	/**
	 * @param  string
	 * @return
	 */
	private byte[] loadData(final String classpathData) {

		try (InputStream in = getClass().getResourceAsStream(classpathData); StringWriter sw = new StringWriter()) {

			return in.readAllBytes();
		} catch (IOException e) {

			e.printStackTrace();
			throw new RuntimeException("Test nicht möglich: " + e.getMessage());
		}
	}

}
