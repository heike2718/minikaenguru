// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.uploads;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
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
import de.egladil.web.mk_gateway.domain.uploads.UploadManager;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.uploads.impl.UploadManagerImpl;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UploadHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * UploadManagerImplIT
 */
public class UploadManagerImplIT extends AbstractIntegrationTest {

	private static final String PATH_UPLOAD_DIR = "/home/heike/mkv/upload";

	private UploadManager uploadManager;

	private KinderRepository kinderRepository;

	private UploadRepository uploadRepository;

	private LoesungszettelRepository loesungszettelRepository;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		uploadManager = UploadManagerImpl.createForIntegrationTests(entityManager);
		kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		uploadRepository = UploadHibernateRepository.createForIntegrationTests(entityManager);
		loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);

	}

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
			.withNachnameAlsZusatz(false).withSprache(Sprache.de);

		UploadRequestPayload uploadRequestPayload = new UploadRequestPayload().withContext(contextObject).withUploadData(uploadData)
			.withTeilnahmenummer(schulkuerzel).withUploadType(UploadType.KLASSENLISTE)
			.withBenutzerID(new Identifier(benutzerUuid));

		// Act
		EntityTransaction transaction = startTransaction();
		ResponsePayload result = uploadManager.processUpload(uploadRequestPayload, Rolle.LEHRER);
		commit(transaction);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Einige Kinder konnten nicht importiert werden. Einen Fehlerreport können Sie mit dem Link herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert.",
			messagePayload.getMessage());

		KlassenlisteImportReport importReport = (KlassenlisteImportReport) result.getData();
		assertEquals(Long.valueOf(2L), Long.valueOf(importReport.getAnzahlDubletten()));
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

		List<String> fehlermeldungen = importReport.getNichtImportierteZeilen();
		assertEquals(1, fehlermeldungen.size());
		assertEquals(
			"Fehler! Zeile \"2a,Heinz,2\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen.",
			fehlermeldungen.get(0));

		List<PersistenterUpload> uploads = uploadRepository.findUploadsWithTeilnahmenummer(schulkuerzel);

		assertEquals(1, uploads.size());

		PersistenterUpload persistenterUpload = uploads.get(0);
		assertEquals(UploadStatus.DATENFEHLER, persistenterUpload.getStatus());

		String path = PATH_UPLOAD_DIR + File.separator + persistenterUpload.getUuid() + "-fehlerreport.csv";

		File fehlerfile = new File(path);
		assertTrue(fehlerfile.exists());
		assertTrue(fehlerfile.isFile());
		assertTrue(fehlerfile.canRead());
	}

	@Test
	void should_uploadAuswertungByAdmin_work_whenCsv() {

		// Arrange
		String benutzerUuid = "it-db-inside-docker";
		String schulkuerzel = "UUBW0AZW";

		byte[] data = loadData("/auswertungen/auswertung-UUBW0AZW.csv");
		UploadData uploadData = new UploadData("Auswertung-Grundschule Börgitz \"Hans Beimler\".xslx", data);

		UploadAuswertungContext contextObject = new UploadAuswertungContext().withKuerzelLand("DE-ST").withSprache(Sprache.en);

		UploadRequestPayload uploadRequestPayload = new UploadRequestPayload().withContext(contextObject).withUploadData(uploadData)
			.withTeilnahmenummer(schulkuerzel).withUploadType(UploadType.AUSWERTUNG)
			.withBenutzerID(new Identifier(benutzerUuid));

		// Act
		EntityTransaction transaction = startTransaction();
		ResponsePayload result = uploadManager.processUpload(uploadRequestPayload, Rolle.ADMIN);
		commit(transaction);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Die Auswertung wurde erfolgreich importiert. Vielen Dank!", messagePayload.getMessage());

		AuswertungImportReport report = (AuswertungImportReport) result.getData();
		assertTrue(report.getFehlerhafteZeilen().isEmpty());

		List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
		assertEquals(1, teilnahmen.size());

		AnonymisierteTeilnahmeAPIModel teilnahme = teilnahmen.get(0);
		assertEquals(24, teilnahme.anzahlKinder());
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
	void should_uploadAuswertungByAdmin_work_whenExcel() {

		// Arrange
		String benutzerUuid = "it-db-inside-docker";
		String schulkuerzel = "M5ZD2NL2";

		byte[] data = loadData("/auswertungen/auswertung-M5ZD2NL2.xlsx");
		UploadData uploadData = new UploadData("Auswertung-Rosental.xslx", data);

		UploadAuswertungContext contextObject = new UploadAuswertungContext().withKuerzelLand("DE-ST").withSprache(Sprache.de);

		UploadRequestPayload uploadRequestPayload = new UploadRequestPayload().withContext(contextObject).withUploadData(uploadData)
			.withTeilnahmenummer(schulkuerzel).withUploadType(UploadType.AUSWERTUNG)
			.withBenutzerID(new Identifier(benutzerUuid));

		// Act
		EntityTransaction transaction = startTransaction();
		ResponsePayload result = uploadManager.processUpload(uploadRequestPayload, Rolle.ADMIN);
		commit(transaction);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Die Auswertung wurde erfolgreich importiert. Vielen Dank!", messagePayload.getMessage());

		AuswertungImportReport report = (AuswertungImportReport) result.getData();
		assertTrue(report.getFehlerhafteZeilen().isEmpty());

		List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
		assertEquals(1, teilnahmen.size());

		AnonymisierteTeilnahmeAPIModel teilnahme = teilnahmen.get(0);
		assertEquals(15, teilnahme.anzahlKinder());
		TeilnahmeIdentifier teilnahmeIdentifier = teilnahme.identifier();
		assertEquals(2020, teilnahmeIdentifier.jahr());
		assertEquals(schulkuerzel, teilnahmeIdentifier.teilnahmenummer());
		assertEquals(Teilnahmeart.SCHULE, teilnahmeIdentifier.teilnahmeart());

		List<Loesungszettel> alleLoesungszettel = loesungszettelRepository.loadAll(teilnahmeIdentifier);

		for (Loesungszettel loesungszettel : alleLoesungszettel) {

			assertEquals(Auswertungsquelle.UPLOAD, loesungszettel.auswertungsquelle());
			assertEquals("DE-ST", loesungszettel.landkuerzel());
			assertEquals(Klassenstufe.ZWEI, loesungszettel.klassenstufe());
			assertEquals(Sprache.de, loesungszettel.sprache());
			LoesungszettelRohdaten rohdaten = loesungszettel.rohdaten();
			assertFalse(rohdaten.hatTypo());
			assertEquals(rohdaten.nutzereingabe(), rohdaten.wertungscode());
			assertNull(rohdaten.antwortcode());

		}

	}

	/**
	 * @param  string
	 * @return
	 */
	private byte[] loadData(final String classpathData) {

		try (InputStream in = getClass().getResourceAsStream(classpathData)) {

			return in.readAllBytes();
		} catch (IOException e) {

			e.printStackTrace();
			throw new RuntimeException("Test nicht möglich: " + e.getMessage());
		}
	}

}
