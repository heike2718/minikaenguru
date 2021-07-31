// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
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
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;

/**
 * AuswertungImportServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class AuswertungImportServiceTest {

	private static final String SCHULKUERZEL = "ZUTFG654F";

	private static final String BENUTZER_UUID = "9515636f-9909-45b2-9132-85e2e0af3cb4";

	private static final String KUERZEL_LAND = "DE-HE";

	private static final Integer JAHR_WETTBEWERB_BEENDET = Integer.valueOf(2019);

	private static final Integer JAHR_WETTBEWERB_RUNNING = Integer.valueOf(2021);

	private static final Integer JAHR_WETTBEWERB_NICHT_EXISTENT = Integer.valueOf(2006);

	private static final Sprache SPRACHE = Sprache.de;

	private PersistenterUpload persistenterUpload;

	private UploadAuswertungContext uploadContxtWettbewerbBeendet;

	private UploadAuswertungContext uploadContextWettbewerbRunning;

	private UploadAuswertungContext uploadContextFalscheJahr;

	private UploadAuswertungContext uploadContextOhneJahr;

	private List<Wettbewerb> wettbewerbe = new ArrayList<>();

	@Mock
	private WettbewerbRepository wettbewerbRepository;

	@Mock
	private UploadRepository uploadRepository;

	@Mock
	private LoesungszettelRepository loesungszettelRepository;

	@Mock
	private AnonymisierteTeilnahmenService anonymisierteTeilnahmenService;

	@InjectMocks
	private AuswertungImportService service;

	@BeforeEach
	void setUp() {

		uploadContxtWettbewerbBeendet = new UploadAuswertungContext().withKuerzelLand(KUERZEL_LAND).withSprache(SPRACHE)
			.withWettbewerbsjahr(JAHR_WETTBEWERB_BEENDET);

		uploadContextWettbewerbRunning = new UploadAuswertungContext().withKuerzelLand(KUERZEL_LAND).withSprache(SPRACHE)
			.withWettbewerbsjahr(JAHR_WETTBEWERB_RUNNING);

		uploadContextFalscheJahr = new UploadAuswertungContext().withKuerzelLand(KUERZEL_LAND).withSprache(SPRACHE)
			.withWettbewerbsjahr(JAHR_WETTBEWERB_NICHT_EXISTENT);

		uploadContextOhneJahr = new UploadAuswertungContext().withKuerzelLand(KUERZEL_LAND).withSprache(SPRACHE);

		persistenterUpload = new PersistenterUpload();
		persistenterUpload.setUuid("auswertung");
		persistenterUpload.setBenutzerUuid(BENUTZER_UUID);
		persistenterUpload.setTeilnahmenummer(SCHULKUERZEL);

		wettbewerbe.add(new Wettbewerb(new WettbewerbID(JAHR_WETTBEWERB_BEENDET)).withStatus(WettbewerbStatus.BEENDET));
		wettbewerbe.add(new Wettbewerb(new WettbewerbID(JAHR_WETTBEWERB_RUNNING)).withStatus(WettbewerbStatus.DOWNLOAD_PRIVAT));
	}

	@Nested
	class ImportTests {

		@Test
		void should_importiereAuswertungenReturnErrorPayload_when_wettbewerbMitJahrNichtVorhanden() {

			// Arrange
			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);

			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(wettbewerbe);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextFalscheJahr, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals("2006 gab es keinen Wettbewerb", messagePayload.getMessage());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
		}

		@Test
		void should_importiereAuswertungenReturnExistingTeilnahmen_when_StatusIMPORTIERT() {

			// Arrange
			Pair<Integer, Integer> jahrUndAnzahl = Pair.of(JAHR_WETTBEWERB_RUNNING, Integer.valueOf(13));
			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(
				Collections.singletonList(jahrUndAnzahl));

			persistenterUpload.setStatus(UploadStatus.IMPORTIERT);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload);

			// Assert
			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(1, teilnahmen.size());
			AnonymisierteTeilnahmeAPIModel teilnahme = teilnahmen.get(0);
			assertEquals(13, teilnahme.anzahlKinder());

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert. Vielen Dank!",
				messagePayload.getMessage());

			verify(wettbewerbRepository, never()).loadWettbewerbe();
			verify(uploadRepository, never()).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);

		}

		@Test
		void should_importiereAuswertungen_work() {

			// Arrange
			service.pathUploadDir = "/home/heike/upload/auswertungen-testdaten/korrekt";

			List<Pair<Integer, Integer>> jahreUndAnzahlen = new ArrayList<>();
			jahreUndAnzahlen.add(Pair.of(JAHR_WETTBEWERB_BEENDET, Integer.valueOf(12)));
			jahreUndAnzahlen.add(Pair.of(JAHR_WETTBEWERB_RUNNING, Integer.valueOf(13)));

			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(jahreUndAnzahlen);

			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
			persistenterUpload.setDateiname("Auswertung Blümchenschule.xslx");

			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(wettbewerbe);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);
			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(new Loesungszettel());

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload);

			// Assert
			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(2, teilnahmen.size());

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert. Vielen Dank!",
				messagePayload.getMessage());

			assertTrue(report.getFehlerhafteZeilen().isEmpty());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, times(24)).addLoesungszettel(any());

		}

		@Test
		void should_importiereAuswertungenNotPersistAnyLoesungszettel_when_eineFalscheZeile() {

			// Arrange
			File file = new File(
				"/home/heike/upload/auswertungen-testdaten/fehlerhaft/mit-ueberschrift-fehlerhaft-fehlerreport.csv");

			FileUtils.deleteQuietly(file);

			service.pathUploadDir = "/home/heike/upload/auswertungen-testdaten/fehlerhaft";

			List<Pair<Integer, Integer>> jahreUndAnzahlen = new ArrayList<>();
			jahreUndAnzahlen.add(Pair.of(JAHR_WETTBEWERB_BEENDET, Integer.valueOf(12)));

			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(jahreUndAnzahlen);

			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
			persistenterUpload.setUuid("mit-ueberschrift-fehlerhaft");
			persistenterUpload.setDateiname("Auswertung Blümchenschule.xslx");

			uploadContextWettbewerbRunning.setRolle(Rolle.LEHRER);

			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(wettbewerbe);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich hochgeladen. Sie muss noch nachbearbeitet werden. Die Statistik steht Ihnen in einigen Tagen zur Verfügung.",
				messagePayload.getMessage());

			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(1, teilnahmen.size());

			List<String> fehlermeldungen = report.getFehlerhafteZeilen();
			assertEquals(0, fehlermeldungen.size());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, never()).addLoesungszettel(any());

			assertTrue(file.exists());

		}

		@Test
		void should_importiereAuswertungenNotPersistAnyLoesungszettel_when_ohneUeberschrift() {

			// Arrange
			service.pathUploadDir = "/home/heike/upload/auswertungen-testdaten/fehlerhaft";

			List<Pair<Integer, Integer>> jahreUndAnzahlen = new ArrayList<>();
			jahreUndAnzahlen.add(Pair.of(JAHR_WETTBEWERB_BEENDET, Integer.valueOf(12)));

			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(jahreUndAnzahlen);

			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
			persistenterUpload.setUuid("ohne-ueberschrift");
			persistenterUpload.setDateiname("Auswertung Blümchenschule.xslx");

			uploadContextWettbewerbRunning.setRolle(Rolle.LEHRER);

			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(wettbewerbe);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich hochgeladen. Sie muss noch nachbearbeitet werden. Die Statistik steht Ihnen in einigen Tagen zur Verfügung.",
				messagePayload.getMessage());

			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(1, teilnahmen.size());

			List<String> fehlermeldungen = report.getFehlerhafteZeilen();
			assertEquals(0, fehlermeldungen.size());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, never()).addLoesungszettel(any());
		}
	}

	@Nested
	class LehrerImportTests {

		@Test
		void should_importiereAuswertungenReturnExistingTeilnahmen_when_StatusLEER() {

			// Arrange
			service.pathUploadDir = "/home/heike/upload/auswertungen-testdaten/fehlerhaft";

			Pair<Integer, Integer> jahrUndAnzahl = Pair.of(JAHR_WETTBEWERB_BEENDET, Integer.valueOf(12));
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

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload);

			// Assert
			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(1, teilnahmen.size());
			AnonymisierteTeilnahmeAPIModel teilnahme = teilnahmen.get(0);
			assertEquals(12, teilnahme.anzahlKinder());
			assertEquals(JAHR_WETTBEWERB_BEENDET.intValue(), teilnahme.identifier().jahr());

			assertTrue(report.getFehlerhafteZeilen().isEmpty());

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Die Datei \"Auswertung Blümchenschule.xslx\" war leider leer.",
				messagePayload.getMessage());

			verify(wettbewerbRepository, never()).loadWettbewerbe();
			verify(uploadRepository, never()).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, never()).addLoesungszettel(any());

		}

		@Test
		void should_importiereAuswertungenReturnErrorPayload_when_wettbewerbBeendet() {

			// Arrange
			uploadContxtWettbewerbBeendet.setRolle(Rolle.LEHRER);
			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);

			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(wettbewerbe);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContxtWettbewerbBeendet, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals(
				"Auswertungen können nicht mehr hochgeladen werden, da der Wettbewerb beendet ist. Bitte senden Sie Ihre Auswertungen per Mail an minikaenguru@egladil.de.",
				messagePayload.getMessage());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
		}

		@Test
		void should_importiereAuswertungenReturnErrorPayload_when_wettbewrbsjahrNullAndAktuellerWettbewerbBeendet() {

			// Arrange
			uploadContextOhneJahr.setRolle(Rolle.LEHRER);
			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);

			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(Collections.singletonList(wettbewerbe.get(0)));
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextOhneJahr, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals(
				"Auswertungen können nicht mehr hochgeladen werden, da der Wettbewerb beendet ist. Bitte senden Sie Ihre Auswertungen per Mail an minikaenguru@egladil.de.",
				messagePayload.getMessage());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
		}

		// @Test
		void should_importiereAuswertungWork_when_wettbewebRunning() {

			// Arrange
			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);

			uploadContextWettbewerbRunning.setRolle(Rolle.LEHRER);
			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(wettbewerbe);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContxtWettbewerbBeendet, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert.",
				messagePayload.getMessage());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);

		}

		@Test
		void should_importiereAuswertungenWork_when_WettbewerbsjahrNullAndAktuellerWettbewerbRunningUndStatusHOCHGELADEN() {

			// Arrange
			service.pathUploadDir = "/home/heike/upload/auswertungen-testdaten/korrekt";
			uploadContextOhneJahr.setRolle(Rolle.LEHRER);

			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(wettbewerbe);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);

			List<Pair<Integer, Integer>> jahreUndAnzahl = new ArrayList<>();
			jahreUndAnzahl.add(Pair.of(JAHR_WETTBEWERB_BEENDET, 13));
			jahreUndAnzahl.add(Pair.of(JAHR_WETTBEWERB_RUNNING, 12));

			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(jahreUndAnzahl);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(new Loesungszettel());

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextOhneJahr, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert. Vielen Dank!",
				messagePayload.getMessage());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, times(24)).addLoesungszettel(any());

			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(2, teilnahmen.size());

			{

				AnonymisierteTeilnahmeAPIModel teilnahme = teilnahmen.get(0);
				assertEquals(JAHR_WETTBEWERB_BEENDET.intValue(), teilnahme.identifier().jahr());
				assertEquals(13, teilnahme.anzahlKinder());
			}

			{

				AnonymisierteTeilnahmeAPIModel teilnahme = teilnahmen.get(1);
				assertEquals(JAHR_WETTBEWERB_RUNNING.intValue(), teilnahme.identifier().jahr());
				assertEquals(12, teilnahme.anzahlKinder());
			}

			assertTrue(report.getFehlerhafteZeilen().isEmpty());
		}
	}

	@Nested
	class AdminImportTests {

		@Test
		void should_importiereAuswertungenReturnErrorPayload_when_wettbewerbMitJahrNichtVorhanden() {

			// Arrange
			uploadContextFalscheJahr.setRolle(Rolle.ADMIN);
			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(wettbewerbe);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextFalscheJahr, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals("2006 gab es keinen Wettbewerb", messagePayload.getMessage());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
		}

		@Test
		void should_importiereAuswertungenWork_when_wettbewerbBeendetUndStatusHOCHGELADEN() {

			// Arrange
			service.pathUploadDir = "/home/heike/upload/auswertungen-testdaten/korrekt";
			uploadContxtWettbewerbBeendet.setRolle(Rolle.ADMIN);
			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(wettbewerbe);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);

			Pair<Integer, Integer> jahrUndAnzahl = Pair.of(JAHR_WETTBEWERB_BEENDET, 13);
			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(
				Collections.singletonList(jahrUndAnzahl));
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(new Loesungszettel());

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContxtWettbewerbBeendet, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert. Vielen Dank!",
				messagePayload.getMessage());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, times(24)).addLoesungszettel(any());

			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(1, teilnahmen.size());

			AnonymisierteTeilnahmeAPIModel teilnahme = teilnahmen.get(0);
			assertEquals(JAHR_WETTBEWERB_BEENDET.intValue(), teilnahme.identifier().jahr());
			assertEquals(13, teilnahme.anzahlKinder());

			assertTrue(report.getFehlerhafteZeilen().isEmpty());
		}

		@Test
		void should_importiereAuswertungenWork_when_WettbewerbsjahrNullWettbewerbBeendetUndStatusHOCHGELADEN() {

			// Arrange
			service.pathUploadDir = "/home/heike/upload/auswertungen-testdaten/korrekt";

			uploadContextOhneJahr.setRolle(Rolle.ADMIN);

			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(Collections.singletonList(wettbewerbe.get(0)));
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);

			Pair<Integer, Integer> jahrUndAnzahl = Pair.of(JAHR_WETTBEWERB_BEENDET, 13);
			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(
				Collections.singletonList(jahrUndAnzahl));
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(new Loesungszettel());

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextOhneJahr, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert. Vielen Dank!",
				messagePayload.getMessage());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, times(24)).addLoesungszettel(any());

			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(1, teilnahmen.size());

			AnonymisierteTeilnahmeAPIModel teilnahme = teilnahmen.get(0);
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

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload);

			// Assert
			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(1, teilnahmen.size());
			AnonymisierteTeilnahmeAPIModel teilnahme = teilnahmen.get(0);
			assertEquals(13, teilnahme.anzahlKinder());

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Die Auswertung wurde erfolgreich importiert. Vielen Dank!",
				messagePayload.getMessage());

			verify(wettbewerbRepository, never()).loadWettbewerbe();
			verify(uploadRepository, never()).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);

		}

		@Test
		void should_importiereAuswertungenReturnExistingTeilnahmen_when_StatusLEER() {

			// Arrange
			service.pathUploadDir = "/home/heike/upload/auswertungen-testdaten/fehlerhaft";

			Pair<Integer, Integer> jahrUndAnzahl = Pair.of(JAHR_WETTBEWERB_BEENDET, Integer.valueOf(12));
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

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload);

			// Assert
			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(1, teilnahmen.size());
			AnonymisierteTeilnahmeAPIModel teilnahme = teilnahmen.get(0);
			assertEquals(12, teilnahme.anzahlKinder());
			assertEquals(JAHR_WETTBEWERB_BEENDET.intValue(), teilnahme.identifier().jahr());

			assertTrue(report.getFehlerhafteZeilen().isEmpty());

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Auswertung leer oder fehlerhaft. Upload-ID=mit-ueberschrift-leer, Teilnahmenummer=ZUTFG654F",
				messagePayload.getMessage());
			verify(wettbewerbRepository, never()).loadWettbewerbe();
			verify(uploadRepository, never()).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, never()).addLoesungszettel(any());
		}

		@Test
		void should_importiereAuswertungenNotPersistAnyLoesungszettel_when_eineFalscheZeile() {

			// Arrange
			File file = new File(
				"/home/heike/upload/auswertungen-testdaten/fehlerhaft/mit-ueberschrift-fehlerhaft-fehlerreport.csv");

			FileUtils.deleteQuietly(file);

			service.pathUploadDir = "/home/heike/upload/auswertungen-testdaten/fehlerhaft";

			uploadContextWettbewerbRunning.setRolle(Rolle.ADMIN);

			List<Pair<Integer, Integer>> jahreUndAnzahlen = new ArrayList<>();
			jahreUndAnzahlen.add(Pair.of(JAHR_WETTBEWERB_BEENDET, Integer.valueOf(12)));

			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(jahreUndAnzahlen);

			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
			persistenterUpload.setUuid("mit-ueberschrift-fehlerhaft");
			persistenterUpload.setDateiname("Auswertung Blümchenschule.xslx");

			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(wettbewerbe);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals(
				"Die Datei \"Auswertung Blümchenschule.xslx\" enthält fehlerhafte Zeilen. Upload-ID=mit-ueberschrift-fehlerhaft, Teilnahmenummer=ZUTFG654F",
				messagePayload.getMessage());

			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(1, teilnahmen.size());

			List<String> fehlermeldungen = report.getFehlerhafteZeilen();
			assertEquals(1, fehlermeldungen.size());
			assertEquals(
				"Fehler Zeile 4! [laenge wertungscode rrfrfrrrfff (11) und klassenstufe ZWEI sind inkompatibel] (Rohdaten=Maylin,r,3,r,3.0,f,-0.75,r,3.0,f,-0.75,r,4.0,r,4.0,r,4.0,f,-1.0,f,-1.0,f,-1.25,32.5)",
				fehlermeldungen.get(0));

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, never()).addLoesungszettel(any());

			assertTrue(file.exists());

		}

		@Test
		void should_importiereAuswertungenNotPersistAnyLoesungszettel_when_ohneUeberschrift() {

			// Arrange
			service.pathUploadDir = "/home/heike/upload/auswertungen-testdaten/fehlerhaft";

			List<Pair<Integer, Integer>> jahreUndAnzahlen = new ArrayList<>();
			jahreUndAnzahlen.add(Pair.of(JAHR_WETTBEWERB_BEENDET, Integer.valueOf(12)));

			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = createAnonymisierteTeilnahmen(jahreUndAnzahlen);

			persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
			persistenterUpload.setUuid("ohne-ueberschrift");
			persistenterUpload.setDateiname("Auswertung Blümchenschule.xslx");

			uploadContextWettbewerbRunning.setRolle(Rolle.ADMIN);

			when(wettbewerbRepository.loadWettbewerbe()).thenReturn(wettbewerbe);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);
			when(anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID))
				.thenReturn(anonymisierteTeilnahmen);

			// Act
			ResponsePayload responsePayload = service.importiereAuswertung(uploadContextWettbewerbRunning, persistenterUpload);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Bei der Datei \"Auswertung Blümchenschule.xslx\" fehlt die Überscrift. Upload-ID=ohne-ueberschrift, Teilnahmenummer=ZUTFG654F",
				messagePayload.getMessage());

			AuswertungImportReport report = (AuswertungImportReport) responsePayload.getData();
			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = report.getTeilnahmen();
			assertEquals(1, teilnahmen.size());

			List<String> fehlermeldungen = report.getFehlerhafteZeilen();
			assertEquals(0, fehlermeldungen.size());

			verify(wettbewerbRepository).loadWettbewerbe();
			verify(uploadRepository).updateUpload(persistenterUpload);
			verify(anonymisierteTeilnahmenService).loadAnonymisierteTeilnahmen(SCHULKUERZEL, BENUTZER_UUID);
			verify(loesungszettelRepository, never()).addLoesungszettel(any());
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
