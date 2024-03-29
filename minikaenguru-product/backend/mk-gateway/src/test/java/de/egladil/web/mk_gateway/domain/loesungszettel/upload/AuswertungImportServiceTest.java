// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.AnonymisierteTeilnahmenService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * AuswertungImportServiceTest
 */
@QuarkusTest
public class AuswertungImportServiceTest {

	private static final String LOESUNGSBUCHSTABEN_IKID = "AB-CB-CB";

	private static final String LOESUNGSBUCHSTABEN_EINS = "CDDE-EDBB-ADCE";

	private static final String LOESUNGSBUCHSTABEN_ZWEI = "CDCEA-DEABB-BEABA";

	private static final String SCHULKUERZEL = "ZUTFG654F";

	private static final String BENUTZER_UUID = "9515636f-9909-45b2-9132-85e2e0af3cb4";

	private static final String KUERZEL_LAND = "DE-HE";

	private static final Integer JAHR_WETTBEWERB_BEENDET = Integer.valueOf(2019);

	private static final Integer JAHR_WETTBEWERB_RUNNING = Integer.valueOf(2021);

	private static final Sprache SPRACHE = Sprache.de;

	private PersistenterUpload persistenterUpload;

	private UploadAuswertungContext uploadContxtWettbewerbBeendet;

	private UploadAuswertungContext uploadContextWettbewerbRunning;

	private List<Wettbewerb> wettbewerbe = new ArrayList<>();

	@InjectMock
	private UploadRepository uploadRepository;

	@InjectMock
	private LoesungszettelRepository loesungszettelRepository;

	@InjectMock
	private AnonymisierteTeilnahmenService anonymisierteTeilnahmenService;

	@Inject
	private AuswertungImportService service;

	@BeforeEach
	void setUp() {

		wettbewerbe.add(new Wettbewerb(new WettbewerbID(JAHR_WETTBEWERB_BEENDET)).withStatus(WettbewerbStatus.BEENDET)
			.withLoesungsbuchstabenIKids(LOESUNGSBUCHSTABEN_IKID).withLoesungsbuchstabenKlasse1(LOESUNGSBUCHSTABEN_EINS)
			.withLoesungsbuchstabenKlasse2(LOESUNGSBUCHSTABEN_ZWEI));
		wettbewerbe.add(new Wettbewerb(new WettbewerbID(JAHR_WETTBEWERB_RUNNING)).withStatus(WettbewerbStatus.DOWNLOAD_PRIVAT)
			.withLoesungsbuchstabenIKids(LOESUNGSBUCHSTABEN_IKID).withLoesungsbuchstabenKlasse1(LOESUNGSBUCHSTABEN_EINS)
			.withLoesungsbuchstabenKlasse2(LOESUNGSBUCHSTABEN_ZWEI));

		uploadContxtWettbewerbBeendet = new UploadAuswertungContext().withKuerzelLand(KUERZEL_LAND).withSprache(SPRACHE)
			.withWettbewerb(wettbewerbe.get(0));

		uploadContextWettbewerbRunning = new UploadAuswertungContext().withKuerzelLand(KUERZEL_LAND).withSprache(SPRACHE)
			.withWettbewerb(wettbewerbe.get(1));

		persistenterUpload = new PersistenterUpload();
		persistenterUpload.setUuid("auswertung");
		persistenterUpload.setBenutzerUuid(BENUTZER_UUID);
		persistenterUpload.setTeilnahmenummer(SCHULKUERZEL);

	}

	@Nested
	class LehrerImportTests {

		@Test
		void should_importiereAuswertungenWork_when_StatusLEER() {

			// Arrange

			Pair<Integer, Integer> jahrUndAnzahl = Pair.of(JAHR_WETTBEWERB_RUNNING, Integer.valueOf(12));
			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(
				Collections.singletonList(jahrUndAnzahl));

			persistenterUpload.setStatus(UploadStatus.LEER);
			persistenterUpload.setDateiname("Auswertung Blümchenschule.xslx");
			persistenterUpload.setUuid("mit-ueberschrift-leer");

			uploadContextWettbewerbRunning.setRolle(Rolle.LEHRER);

			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			List<String> lines = MkGatewayFileUtils.readLinesFromClasspath("/upload/auswertungen/auswertung-leer.csv");

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload,
				lines);

			// Assert
			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);
			assertEquals(12, teilnahme.anzahlKinder());
			assertEquals(JAHR_WETTBEWERB_RUNNING.intValue(), teilnahme.identifier().jahr());

			assertTrue(report.getFehlerhafteZeilen().isEmpty());

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Die Datei \"Auswertung Blümchenschule.xslx\" war leider leer.",
				messagePayload.getMessage());

			verify(uploadRepository, never()).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, never()).addLoesungszettel(any());

		}

		@Test
		void should_importiereAuswertungen_work() {

			// Arrange
			List<Pair<Integer, Integer>> jahreUndAnzahlen = new ArrayList<>();
			jahreUndAnzahlen.add(Pair.of(JAHR_WETTBEWERB_BEENDET, Integer.valueOf(12)));
			jahreUndAnzahlen.add(Pair.of(JAHR_WETTBEWERB_RUNNING, Integer.valueOf(13)));

			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(jahreUndAnzahlen);

			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
			persistenterUpload.setDateiname("Auswertung Blümchenschule.xslx");

			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);
			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(new Loesungszettel());

			List<String> lines = MkGatewayFileUtils.readLinesFromClasspath("/upload/auswertungen/auswertung-from-excel.csv");

			uploadContextWettbewerbRunning.setRolle(Rolle.LEHRER);

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload,
				lines);

			// Assert
			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert. Vielen Dank!",
				messagePayload.getMessage());

			assertTrue(report.getFehlerhafteZeilen().isEmpty());

			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, times(8)).addLoesungszettel(any());

		}

		@Test
		void should_importiereAuswertungenReturnErrorPayload_when_wettbewerbBeendet() {

			// Arrange
			uploadContxtWettbewerbBeendet.setRolle(Rolle.LEHRER);
			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);

			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);

			List<String> lines = MkGatewayFileUtils.readLinesFromClasspath("/upload/auswertungen/auswertung-from-excel.csv");

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContxtWettbewerbBeendet, persistenterUpload,
				lines);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals(
				"Auswertungen können nicht mehr hochgeladen werden, da der Wettbewerb beendet ist. Bitte senden Sie Ihre Auswertungen per Mail an minikaenguru@egladil.de.",
				messagePayload.getMessage());

			verify(uploadRepository).updateUpload(persistenterUpload);
		}

		// @Test
		void should_importiereAuswertungWork_when_wettbewebRunning() {

			// Arrange
			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);

			uploadContextWettbewerbRunning.setRolle(Rolle.LEHRER);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);

			List<String> lines = MkGatewayFileUtils.readLinesFromClasspath("/upload/auswertungen/auswertung-from-excel.csv");

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContxtWettbewerbBeendet, persistenterUpload,
				lines);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert.",
				messagePayload.getMessage());

			verify(uploadRepository).updateUpload(persistenterUpload);

		}

		@Test
		void should_importiereAuswertungenNotPersistAnyLoesungszettel_when_eineFalscheZeile() {

			// Arrange
			List<Pair<Integer, Integer>> jahreUndAnzahlen = new ArrayList<>();
			jahreUndAnzahlen.add(Pair.of(JAHR_WETTBEWERB_RUNNING, Integer.valueOf(12)));

			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(jahreUndAnzahlen);

			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
			persistenterUpload.setUuid("mit-ueberschrift-fehlerhaft");
			persistenterUpload.setDateiname("Auswertung Blümchenschule.xslx");

			uploadContextWettbewerbRunning.setRolle(Rolle.LEHRER);

			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			uploadContextWettbewerbRunning.setRolle(Rolle.LEHRER);

			// /upload/auswertungen/auswertung-eine-zeile-falsch.csv
			List<String> lines = MkGatewayFileUtils.readLinesFromClasspath("/upload/auswertungen/auswertung-eine-zeile-falsch.csv");

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload,
				lines);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich hochgeladen. Sie muss noch nachbearbeitet werden. Die Statistik steht Ihnen in einigen Tagen zur Verfügung.",
				messagePayload.getMessage());

			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);

			List<String> fehlermeldungen = report.getFehlerhafteZeilen();
			assertEquals(0, fehlermeldungen.size());

			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, never()).addLoesungszettel(any());

		}
	}

	@Nested
	class AdminImportTests {

		@Test
		void should_importiereAuswertungenWork_when_wettbewerbBeendetUndStatusHOCHGELADEN() {

			// Arrange
			uploadContxtWettbewerbBeendet.setRolle(Rolle.ADMIN);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);

			Pair<Integer, Integer> jahrUndAnzahl = Pair.of(JAHR_WETTBEWERB_BEENDET, 13);
			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(
				Collections.singletonList(jahrUndAnzahl));
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(new Loesungszettel());

			List<String> lines = MkGatewayFileUtils.readLinesFromClasspath("/upload/auswertungen/auswertung-from-excel.csv");

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContxtWettbewerbBeendet, persistenterUpload,
				lines);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert. Vielen Dank!",
				messagePayload.getMessage());

			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, times(8)).addLoesungszettel(any());

			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);
			assertEquals(JAHR_WETTBEWERB_BEENDET.intValue(), teilnahme.identifier().jahr());
			assertEquals(13, teilnahme.anzahlKinder());

			assertTrue(report.getFehlerhafteZeilen().isEmpty());
		}

		@Test
		void should_importiereAuswertungenReturnExistingTeilnahmen_when_StatusIMPORTIERT() {

			// Arrange
			uploadContextWettbewerbRunning.setRolle(Rolle.ADMIN);

			Pair<Integer, Integer> jahrUndAnzahl = Pair.of(JAHR_WETTBEWERB_RUNNING, 13);
			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(
				Collections.singletonList(jahrUndAnzahl));

			persistenterUpload.setStatus(UploadStatus.IMPORTIERT);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			List<String> lines = MkGatewayFileUtils.readLinesFromClasspath("/upload/auswertungen/auswertung-from-excel.csv");

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload,
				lines);

			// Assert
			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);
			assertEquals(13, teilnahme.anzahlKinder());

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert. Vielen Dank!",
				messagePayload.getMessage());

			verify(uploadRepository, never()).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);

		}

		@Test
		void should_importiereAuswertungenReturnExistingTeilnahmen_when_StatusLEER() {

			// Arrange

			Pair<Integer, Integer> jahrUndAnzahl = Pair.of(JAHR_WETTBEWERB_RUNNING, Integer.valueOf(12));
			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(
				Collections.singletonList(jahrUndAnzahl));

			persistenterUpload.setStatus(UploadStatus.LEER);
			persistenterUpload.setDateiname("Auswertung Blümchenschule.xslx");
			persistenterUpload.setUuid("mit-ueberschrift-leer");

			uploadContextWettbewerbRunning.setRolle(Rolle.ADMIN);

			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			List<String> lines = MkGatewayFileUtils.readLinesFromClasspath("/upload/auswertungen/auswertung-leer.csv");

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload,
				lines);

			// Assert
			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);
			assertEquals(12, teilnahme.anzahlKinder());
			assertEquals(JAHR_WETTBEWERB_RUNNING.intValue(), teilnahme.identifier().jahr());

			assertTrue(report.getFehlerhafteZeilen().isEmpty());

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Auswertung leer oder fehlerhaft. Upload-ID=mit-ueberschrift-leer, Teilnahmenummer=ZUTFG654F",
				messagePayload.getMessage());
			verify(uploadRepository, never()).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, never()).addLoesungszettel(any());
		}

		@Test
		void should_importiereAuswertungenNotPersistAnyLoesungszettel_when_eineFalscheZeile() {

			// Arrange
			File file = new File(
				service.uploadsDir + "/mit-ueberschrift-fehlerhaft-fehlerreport.csv");

			FileUtils.deleteQuietly(file);

			uploadContextWettbewerbRunning.setRolle(Rolle.ADMIN);

			List<Pair<Integer, Integer>> jahreUndAnzahlen = new ArrayList<>();
			jahreUndAnzahlen.add(Pair.of(JAHR_WETTBEWERB_RUNNING, Integer.valueOf(12)));

			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(jahreUndAnzahlen);

			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
			persistenterUpload.setUuid("mit-ueberschrift-fehlerhaft");
			persistenterUpload.setDateiname("Auswertung Blümchenschule.xslx");

			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			List<String> lines = MkGatewayFileUtils.readLinesFromClasspath("/upload/auswertungen/auswertung-eine-zeile-falsch.csv");

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload,
				lines);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals(
				"Die Datei \"Auswertung Blümchenschule.xslx\" enthält fehlerhafte Zeilen. Upload-ID=mit-ueberschrift-fehlerhaft, Teilnahmenummer=ZUTFG654F",
				messagePayload.getMessage());

			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);

			List<String> fehlermeldungen = report.getFehlerhafteZeilen();
			assertEquals(1, fehlermeldungen.size());
			assertEquals(
				"Fehler Zeile 3! [laenge wertungscode rrrffrrrnnnfff (14) und klassenstufe ZWEI sind inkompatibel] (Rohdaten=Szymon;r;3;r;3.0;r;3.0;f;-0.75;f;-0.75;r;4.0;r;4.0;r;4.0;n;0.0;n;0.0;n;0.0;f;-1.25;f;-1.25;f;-1.25;29.75)",
				fehlermeldungen.get(0));

			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, never()).addLoesungszettel(any());
		}

		@Test
		void should_importiereAuswertungenNotPersistAnyLoesungszettel_when_ohneUeberschrift() {

			// Arrange

			List<Pair<Integer, Integer>> jahreUndAnzahlen = new ArrayList<>();
			jahreUndAnzahlen.add(Pair.of(JAHR_WETTBEWERB_RUNNING, Integer.valueOf(12)));

			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(jahreUndAnzahlen);

			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
			persistenterUpload.setUuid("auswertung-ohne-ueberschrift");
			persistenterUpload.setDateiname("Auswertung Blümchenschule.xslx");

			uploadContextWettbewerbRunning.setRolle(Rolle.ADMIN);

			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			List<String> lines = MkGatewayFileUtils.readLinesFromClasspath("/upload/auswertungen/auswertung-ohne-ueberschrift.csv");

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload,
				lines);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Bei der Datei \"Auswertung Blümchenschule.xslx\" fehlt die Überschrift. Upload-ID=auswertung-ohne-ueberschrift, Teilnahmenummer=ZUTFG654F",
				messagePayload.getMessage());

			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);

			List<String> fehlermeldungen = report.getFehlerhafteZeilen();
			assertEquals(0, fehlermeldungen.size());

			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, never()).addLoesungszettel(any());
		}

		@Test
		void should_importiereAuswertungenWork_when_DateiMitNamenspalte() {

			// Arrange
			uploadContextWettbewerbRunning.setRolle(Rolle.ADMIN);
			persistenterUpload.setUuid("2021_auswertung_minikaenguru_klasse_1");
			persistenterUpload.setDateiname("2021_auswertung_minikaenguru_klasse_1.xlsx");

			Pair<Integer, Integer> jahrUndAnzahl = Pair.of(JAHR_WETTBEWERB_RUNNING, 13);
			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(
				Collections.singletonList(jahrUndAnzahl));

			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			List<String> lines = MkGatewayFileUtils.readLinesFromClasspath("/upload/auswertung-excel-klasse1.csv");

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload,
				lines);

			// Assert
			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			AnonymisierteTeilnahmeAPIModel teilnahme = report.getTeilnahme();
			assertNotNull(teilnahme);
			assertEquals(13, teilnahme.anzahlKinder());

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert. Vielen Dank!",
				messagePayload.getMessage());

			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);

		}
	}

	private List<AnonymisierteTeilnahmeAPIModel> createAnonymisierteTeilnahmen(final List<Pair<Integer, Integer>> jahreUndAnzahlKinder) {

		List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = new ArrayList<>();

		for (Pair<Integer, Integer> jahrUndAnzahlKinder : jahreUndAnzahlKinder) {

			anonymisierteTeilnahmen.add(AnonymisierteTeilnahmeAPIModel
				.create(new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
					.withTeilnahmenummer(SCHULKUERZEL).withWettbewerbID(new WettbewerbID(jahrUndAnzahlKinder.getLeft())))
				.withAnzahlKinder(jahrUndAnzahlKinder.getRight()));

		}
		return anonymisierteTeilnahmen;
	}
}
