// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderService;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenService;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.klassenlisten.api.KlassenlisteImportReport;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;

/**
 * KlassenlisteCSVImportServiceTest
 */
@QuarkusTest
public class KlassenlisteCSVImportServiceTest {

	private static final String SCHULKUERZEL = "ZUTFG654F";

	private static final String BENUTZER_UUID = "9515636f-9909-45b2-9132-85e2e0af3cb4";

	private UploadKlassenlisteContext uploadKlassenlisteContext;

	private PersistenterUpload persistenterUpload;

	private List<Kind> vorhandeneKinder;

	private Wettbewerb wettbewerb;

	@ConfigProperty(name = "path.external.files")
	String pathExternalFiles;

	@InjectMock
	private KlassenService klassenService;

	@InjectMock
	private KinderService kinderService;

	@InjectMock
	private UploadRepository uploadRepository;

	@Inject
	private KlassenlisteCSVImportService service;

	@BeforeEach
	void setUp() {

		boolean nachnamenAlsZusatz = false;
		Sprache sprache = Sprache.de;
		String kuerzelLand = "DE-HE";

		vorhandeneKinder = new ArrayList<>();

		wettbewerb = new Wettbewerb(new WettbewerbID(2020));
		WettbewerbStatus status = wettbewerb.status();

		while (status != WettbewerbStatus.ANMELDUNG) {

			wettbewerb.naechsterStatus();
			status = wettbewerb.status();
		}

		uploadKlassenlisteContext = new UploadKlassenlisteContext().withKuerzelLand(kuerzelLand)
			.withNachnameAlsZusatz(nachnamenAlsZusatz).withSprache(sprache).withWettbewerb(wettbewerb);

		persistenterUpload = new PersistenterUpload();
		persistenterUpload.setUuid("klassenliste");
		persistenterUpload.setBenutzerUuid(BENUTZER_UUID);
		persistenterUpload.setTeilnahmenummer(SCHULKUERZEL);
	}

	@Nested
	class ImportTests {

		@Test
		void should_importiereKlassenHandlePersistenceException() {

			// Arrange
			when(kinderService.findWithSchulteilname(any())).thenThrow(new PersistenceException("böse böse"));
			when(uploadRepository.updateUpload(any())).thenReturn(persistenterUpload);

			// Act
			ResponsePayload result = service.importiereKinder(uploadKlassenlisteContext, persistenterUpload);

			// Assert
			MessagePayload messagePayload = result.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals(
				"Die Klassenliste konnte wegen eines Fehlers leider nicht importiert werden. Bitte senden Sie eine Mail an info@egladil.de.",
				messagePayload.getMessage());

			assertNull(result.getData());

			verify(uploadRepository).updateUpload(any());

		}

		@Test
		void should_importiereKlassenThrowUploadFormatException_when_NullZeilen() {

			// Arrange
			persistenterUpload.setUuid("null-zeilen");
			when(uploadRepository.updateUpload(any())).thenReturn(persistenterUpload);

			List<String> zeilen = MkGatewayFileUtils.readLinesFromClasspath("/upload/klassenlisten/klassenliste-null-zeilen.csv");

			// Act + Assert
			try {

				service.importiereKinder(uploadKlassenlisteContext, persistenterUpload, MkGatewayFileUtils.DEFAULT_ENCODING,
					zeilen);
				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die Klassenliste konnte nicht importiert werden: sie enthält keine Kinder. Bitte prüfen Sie die hochgeladene Datei.",
					e.getMessage());

				verify(klassenService, never()).importiereKlassen(any(), any(), anyList());
				verify(kinderService, never()).importiereKinder(any(), any(), any());
				verify(kinderService, never()).findWithSchulteilname(any());
				verify(klassenService, never()).klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID);
				verify(uploadRepository).updateUpload(persistenterUpload);
			}

		}

		@Test
		void should_importiereKlassenThrowUploadFormatException_when_eineZeileAberKeineUeberschrift() {

			// Arrange
			persistenterUpload.setUuid("eine-zeile-keine-ueberschrift");
			List<String> zeilen = MkGatewayFileUtils
				.readLinesFromClasspath("/upload/klassenlisten/klassenliste-ohne-ueberschrift.csv");

			// Act + Assert
			try {

				service.importiereKinder(uploadKlassenlisteContext, persistenterUpload, MkGatewayFileUtils.DEFAULT_ENCODING,
					zeilen);
				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die Klassenliste kann nicht verarbeitet werden. Es werden genau 4 Spalten mit den Überschriften Klasse,Klassenstufe,Nachname,Vorname in beliebiger Reihenfolge erwartet. Gefunden wurden die Spaltenüberschriften Amiera;Kaled;2a;3.",
					e.getMessage());

				verify(klassenService, never()).importiereKlassen(any(), any(), anyList());
				verify(kinderService, never()).importiereKinder(any(), any(), any());
				verify(kinderService, never()).findWithSchulteilname(any());
				verify(klassenService, never()).klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID);
				verify(uploadRepository, never()).updateUpload(persistenterUpload);
			}

		}

		@Test
		void should_importiereKlassenThrowUploadFormatException_when_alleZeilenFehlerhaft() {

			// Arrange
			persistenterUpload.setUuid("alle-zeilen-falsch");
			List<String> zeilen = MkGatewayFileUtils
				.readLinesFromClasspath("/upload/klassenlisten/klassenliste-alle-falsch.csv");

			// Act + Assert
			try {

				service.importiereKinder(uploadKlassenlisteContext, persistenterUpload, MkGatewayFileUtils.DEFAULT_ENCODING,
					zeilen);
				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die Klassenliste kann nicht verarbeitet werden. Ihre Tabelle hat nicht die erwarteten Spalten. Es werden genau 4 Spalten mit den Überschriften Klasse,Klassenstufe,Nachname,Vorname in beliebiger Reihenfolge erwartet. Gefunden wurden 2 Spaltenüberschrift(en): Vorname;Nachname.",
					e.getMessage());

				verify(klassenService, never()).importiereKlassen(any(), any(), anyList());
				verify(kinderService, never()).importiereKinder(any(), any(), any());
				verify(kinderService, never()).findWithSchulteilname(any());
				verify(klassenService, never()).klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID);
				verify(uploadRepository, never()).updateUpload(persistenterUpload);
			}

		}

		@Test
		void should_importiereKlassenThrowUploadFormatException_when_keineUeberschrift() {

			// Arrange
			persistenterUpload.setUuid("klassenliste-ohne-ueberschrift");

			List<String> zeilen = MkGatewayFileUtils
				.readLinesFromClasspath("/upload/klassenlisten/klassenliste-ohne-ueberschrift.csv");

			// Act + Assert
			try {

				service.importiereKinder(uploadKlassenlisteContext, persistenterUpload, MkGatewayFileUtils.DEFAULT_ENCODING,
					zeilen);
				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die Klassenliste kann nicht verarbeitet werden. Es werden genau 4 Spalten mit den Überschriften Klasse,Klassenstufe,Nachname,Vorname in beliebiger Reihenfolge erwartet. Gefunden wurden die Spaltenüberschriften Amiera;Kaled;2a;3.",
					e.getMessage());

				verify(klassenService, never()).importiereKlassen(any(), any(), anyList());
				verify(kinderService, never()).importiereKinder(any(), any(), any());
				verify(kinderService, never()).findWithSchulteilname(any());
				verify(klassenService, never()).klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID);
				verify(uploadRepository, never()).updateUpload(persistenterUpload);
			}

		}

		@Test
		void should_importiereKlassenWork() throws IOException {

			// Arrange
			List<Klasse> klassen = new ArrayList<>();
			klassen.add(new Klasse(new Identifier("uuid-1a")).withName("1a").withSchuleID(new Identifier(SCHULKUERZEL)));
			klassen.add(new Klasse(new Identifier("uuid-1b")).withName("1b").withSchuleID(new Identifier(SCHULKUERZEL)));
			klassen.add(new Klasse(new Identifier("uuid-2a")).withName("2a").withSchuleID(new Identifier(SCHULKUERZEL)));
			klassen.add(new Klasse(new Identifier("uuid-2b")).withName("2b").withSchuleID(new Identifier(SCHULKUERZEL)));

			List<KlasseAPIModel> klassenAPIModels = klassen.stream().map(k -> KlasseAPIModel.createFromKlasse(k))
				.collect(Collectors.toList());

			List<Kind> kinder = new ArrayList<>();
			kinder.add(
				new Kind(new Identifier("1")).withKlasseID(new Identifier("uuid-1a")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Granach").withSprache(Sprache.de).withVorname("Lukas"));
			kinder.add(
				new Kind(new Identifier("2")).withKlasseID(new Identifier("uuid-1a")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Weiß").withSprache(Sprache.de).withVorname("Natalie"));
			kinder.add(
				new Kind(new Identifier("3")).withKlasseID(new Identifier("uuid-1b")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Wanowski").withSprache(Sprache.de).withVorname("Szymon"));
			kinder.add(
				new Kind(new Identifier("4")).withKlasseID(new Identifier("uuid-1b")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Schöner").withSprache(Sprache.de).withVorname("Patrick"));
			kinder.add(
				new Kind(new Identifier("5")).withKlasseID(new Identifier("uuid-2a")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Hofstedter").withSprache(Sprache.de).withVorname("Lennart"));
			kinder.add(
				new Kind(new Identifier("6")).withKlasseID(new Identifier("uuid-2a")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Fauna").withSprache(Sprache.de).withVorname("Flora"));
			kinder.add(
				new Kind(new Identifier("7")).withKlasseID(new Identifier("uuid-2b")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Gröblin").withSprache(Sprache.de).withVorname("Pauline"));
			kinder.add(
				new Kind(new Identifier("8")).withKlasseID(new Identifier("uuid-2b")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Hinremöller").withSprache(Sprache.de).withVorname("Lucie"));

			when(klassenService.importiereKlassen(any(), any(), anyList())).thenReturn(klassen);
			when(kinderService.importiereKinder(any(), any(), any())).thenReturn(kinder);
			when(kinderService.findWithSchulteilname(any())).thenReturn(vorhandeneKinder);
			when(klassenService.klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID)).thenReturn(klassenAPIModels);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);

			List<String> zeilen = MkGatewayFileUtils
				.readLinesFromClasspath("/upload/klassenlisten/klassenliste-4-klassen.csv");

			// Act
			ResponsePayload responsePayload = service.importiereKinder(uploadKlassenlisteContext, persistenterUpload,
				"CP-1252",
				zeilen);

			MessagePayload messagePayload = responsePayload.getMessage();
			assertTrue(messagePayload.isOk());
			assertEquals("Die Daten wurden erfolgreich importiert. Bitte prüfen Sie, ob Umlaute korrekt angezeigt werden.",
				messagePayload.getMessage());

			assertNotNull(responsePayload.getData());

			KlassenlisteImportReport report = (KlassenlisteImportReport) responsePayload.getData();
			assertEquals(4, report.getKlassen().size());
			assertEquals(8, report.getAnzahlKinderImportiert());
			assertEquals(0L, report.getAnzahlDubletten());
			assertEquals(0L, report.getAnzahlKlassenstufeUnklar());
			assertEquals(0, report.getAnzahlNichtImportiert());
			assertNull(report.getUuidImportReport());
			List<String> fehlermeldungen = report.getFehlerUndWarnungen();
			assertEquals(0, fehlermeldungen.size());

			verify(klassenService).importiereKlassen(any(), any(), anyList());
			verify(kinderService).importiereKinder(any(), any(), any());
			verify(kinderService).findWithSchulteilname(any());
			verify(klassenService).klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID);
			verify(uploadRepository).updateUpload(persistenterUpload);
		}

		@Test
		void should_importiereKlassenIgnoreLeereZeilen() throws IOException {

			// Arrange
			persistenterUpload.setUuid("klassenliste-mit-leerzeilen");

			List<Klasse> klassen = new ArrayList<>();
			klassen.add(new Klasse(new Identifier("uuid-1a")).withName("1a").withSchuleID(new Identifier(SCHULKUERZEL)));
			klassen.add(new Klasse(new Identifier("uuid-1b")).withName("1b").withSchuleID(new Identifier(SCHULKUERZEL)));
			klassen.add(new Klasse(new Identifier("uuid-2a")).withName("2a").withSchuleID(new Identifier(SCHULKUERZEL)));
			klassen.add(new Klasse(new Identifier("uuid-2b")).withName("2b").withSchuleID(new Identifier(SCHULKUERZEL)));

			List<KlasseAPIModel> klassenAPIModels = klassen.stream().map(k -> KlasseAPIModel.createFromKlasse(k))
				.collect(Collectors.toList());

			List<Kind> kinder = new ArrayList<>();
			kinder.add(
				new Kind(new Identifier("1")).withKlasseID(new Identifier("uuid-1a")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Granach").withSprache(Sprache.de).withVorname("Lukas"));
			kinder.add(
				new Kind(new Identifier("2")).withKlasseID(new Identifier("uuid-1a")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Weiß").withSprache(Sprache.de).withVorname("Natalie"));
			kinder.add(
				new Kind(new Identifier("3")).withKlasseID(new Identifier("uuid-1b")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Wanowski").withSprache(Sprache.de).withVorname("Szymon"));
			kinder.add(
				new Kind(new Identifier("4")).withKlasseID(new Identifier("uuid-1b")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Schöner").withSprache(Sprache.de).withVorname("Patrick"));
			kinder.add(
				new Kind(new Identifier("5")).withKlasseID(new Identifier("uuid-2a")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Hofstedter").withSprache(Sprache.de).withVorname("Lennart"));
			kinder.add(
				new Kind(new Identifier("6")).withKlasseID(new Identifier("uuid-2a")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Gfauna").withSprache(Sprache.de).withVorname("Flora"));
			kinder.add(
				new Kind(new Identifier("7")).withKlasseID(new Identifier("uuid-2b")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Gröblin").withSprache(Sprache.de).withVorname("Pauline"));
			kinder.add(
				new Kind(new Identifier("8")).withKlasseID(new Identifier("uuid-2b")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Hinremöller").withSprache(Sprache.de).withVorname("Lucie"));

			when(klassenService.importiereKlassen(any(), any(), anyList())).thenReturn(klassen);
			when(kinderService.importiereKinder(any(), any(), any())).thenReturn(kinder);
			when(kinderService.findWithSchulteilname(any())).thenReturn(vorhandeneKinder);
			when(klassenService.klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID)).thenReturn(klassenAPIModels);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);

			// Act
			ResponsePayload responsePayload = service.importiereKinder(uploadKlassenlisteContext, persistenterUpload);

			MessagePayload messagePayload = responsePayload.getMessage();
			assertTrue(messagePayload.isOk());
			assertEquals("Die Daten wurden erfolgreich importiert. Bitte prüfen Sie, ob Umlaute korrekt angezeigt werden.",
				messagePayload.getMessage());

			assertNotNull(responsePayload.getData());

			KlassenlisteImportReport report = (KlassenlisteImportReport) responsePayload.getData();
			assertEquals(4, report.getKlassen().size());
			assertEquals(8, report.getAnzahlKinderImportiert());
			assertEquals(0L, report.getAnzahlDubletten());
			assertEquals(0L, report.getAnzahlKlassenstufeUnklar());
			assertEquals(0, report.getAnzahlNichtImportiert());
			assertNull(report.getUuidImportReport());
			List<String> fehlermeldungen = report.getFehlerUndWarnungen();
			assertEquals(0, fehlermeldungen.size());

			verify(klassenService).importiereKlassen(any(), any(), anyList());
			verify(kinderService).importiereKinder(any(), any(), any());
			verify(kinderService).findWithSchulteilname(any());
			verify(klassenService).klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID);
			verify(uploadRepository).updateUpload(persistenterUpload);
		}

		@Test
		void should_importiereKlassenAcceptNameInsteadOfNachname() throws IOException {

			// Arrange
			persistenterUpload.setUuid("klassenliste-mit-name-statt-nachname");

			List<Klasse> klassen = new ArrayList<>();
			klassen.add(new Klasse(new Identifier("uuid-1a")).withName("1a").withSchuleID(new Identifier(SCHULKUERZEL)));
			klassen.add(new Klasse(new Identifier("uuid-1b")).withName("1b").withSchuleID(new Identifier(SCHULKUERZEL)));
			klassen.add(new Klasse(new Identifier("uuid-2a")).withName("2a").withSchuleID(new Identifier(SCHULKUERZEL)));
			klassen.add(new Klasse(new Identifier("uuid-2b")).withName("2b").withSchuleID(new Identifier(SCHULKUERZEL)));

			List<KlasseAPIModel> klassenAPIModels = klassen.stream().map(k -> KlasseAPIModel.createFromKlasse(k))
				.collect(Collectors.toList());

			List<Kind> kinder = new ArrayList<>();
			kinder.add(
				new Kind(new Identifier("1")).withKlasseID(new Identifier("uuid-1a")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Granach").withSprache(Sprache.de).withVorname("Lukas"));
			kinder.add(
				new Kind(new Identifier("2")).withKlasseID(new Identifier("uuid-1a")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Weiß").withSprache(Sprache.de).withVorname("Natalie"));
			kinder.add(
				new Kind(new Identifier("3")).withKlasseID(new Identifier("uuid-1b")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Wanowski").withSprache(Sprache.de).withVorname("Szymon"));
			kinder.add(
				new Kind(new Identifier("4")).withKlasseID(new Identifier("uuid-1b")).withKlassenstufe(Klassenstufe.EINS)
					.withLandkuerzel("DE-HE").withNachname("Schöner").withSprache(Sprache.de).withVorname("Patrick"));
			kinder.add(
				new Kind(new Identifier("5")).withKlasseID(new Identifier("uuid-2a")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Hofstedter").withSprache(Sprache.de).withVorname("Lennart"));
			kinder.add(
				new Kind(new Identifier("6")).withKlasseID(new Identifier("uuid-2a")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Gfauna").withSprache(Sprache.de).withVorname("Flora"));
			kinder.add(
				new Kind(new Identifier("7")).withKlasseID(new Identifier("uuid-2b")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Gröblin").withSprache(Sprache.de).withVorname("Pauline"));
			kinder.add(
				new Kind(new Identifier("8")).withKlasseID(new Identifier("uuid-2b")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Hinremöller").withSprache(Sprache.de).withVorname("Lucie"));

			when(klassenService.importiereKlassen(any(), any(), anyList())).thenReturn(klassen);
			when(kinderService.importiereKinder(any(), any(), any())).thenReturn(kinder);
			when(kinderService.findWithSchulteilname(any())).thenReturn(vorhandeneKinder);
			when(klassenService.klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID)).thenReturn(klassenAPIModels);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);

			// Act
			ResponsePayload responsePayload = service.importiereKinder(uploadKlassenlisteContext, persistenterUpload);

			MessagePayload messagePayload = responsePayload.getMessage();
			assertTrue(messagePayload.isOk());
			assertEquals("Die Daten wurden erfolgreich importiert. Bitte prüfen Sie, ob Umlaute korrekt angezeigt werden.",
				messagePayload.getMessage());

			assertNotNull(responsePayload.getData());

			KlassenlisteImportReport report = (KlassenlisteImportReport) responsePayload.getData();
			assertEquals(4, report.getKlassen().size());
			assertEquals(8, report.getAnzahlKinderImportiert());
			assertEquals(0L, report.getAnzahlDubletten());
			assertEquals(0L, report.getAnzahlKlassenstufeUnklar());
			assertEquals(0, report.getAnzahlNichtImportiert());
			assertNull(report.getUuidImportReport());
			List<String> fehlermeldungen = report.getFehlerUndWarnungen();
			assertEquals(0, fehlermeldungen.size());

			verify(klassenService).importiereKlassen(any(), any(), anyList());
			verify(kinderService).importiereKinder(any(), any(), any());
			verify(kinderService).findWithSchulteilname(any());
			verify(klassenService).klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID);
			verify(uploadRepository).updateUpload(persistenterUpload);
		}

		@Test
		void should_importiereKlassenWork_withFehlern() throws IOException {

			// Arrange
			persistenterUpload.setUuid("mit-ueberschrift-alle-anderen-faelle");

			List<Klasse> klassen = new ArrayList<>();
			klassen.add(new Klasse(new Identifier("uuid-2a")).withName("2a").withSchuleID(new Identifier(SCHULKUERZEL)));
			klassen.add(new Klasse(new Identifier("uuid-2b")).withName("2b").withSchuleID(new Identifier(SCHULKUERZEL)));

			List<KlasseAPIModel> klassenAPIModels = klassen.stream().map(k -> KlasseAPIModel.createFromKlasse(k))
				.collect(Collectors.toList());

			List<Kind> kinder = new ArrayList<>();
			kinder.add(
				new Kind(new Identifier("shdiqhio")).withKlasseID(new Identifier("uuid-2a")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Fichtenholz").withSprache(Sprache.de).withVorname("Genadi"));

			vorhandeneKinder.add(
				new Kind(new Identifier("lhsdugui")).withKlasseID(new Identifier("uuid-2a")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Lang").withSprache(Sprache.de).withVorname("Ellen"));

			when(klassenService.importiereKlassen(any(), any(), anyList())).thenReturn(klassen);
			when(kinderService.importiereKinder(any(), any(), any())).thenReturn(kinder);
			when(kinderService.findWithSchulteilname(any())).thenReturn(vorhandeneKinder);
			when(klassenService.klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID)).thenReturn(klassenAPIModels);
			when(uploadRepository.updateUpload(persistenterUpload)).thenReturn(persistenterUpload);

			// Act
			ResponsePayload responsePayload = service.importiereKinder(uploadKlassenlisteContext, persistenterUpload);

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Einige Kinder konnten nicht importiert werden. Den Fehlerreport können Sie herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert. Bitte prüfen Sie außerdem, ob Umlaute korrekt angezeigt werden.",
				messagePayload.getMessage());

			assertNotNull(responsePayload.getData());

			KlassenlisteImportReport report = (KlassenlisteImportReport) responsePayload.getData();
			assertEquals(2, report.getKlassen().size());
			assertEquals(1, report.getAnzahlKinderImportiert());
			assertEquals(2L, report.getAnzahlDubletten());
			assertEquals(1L, report.getAnzahlKlassenstufeUnklar());
			assertEquals(2, report.getAnzahlNichtImportiert());
			assertEquals("mit-ueberschrift-alle-anderen-faelle", report.getUuidImportReport());
			List<String> fehlerUndWarnungen = report.getFehlerUndWarnungen();

			fehlerUndWarnungen.stream().forEach(m -> System.out.println(m));

			assertEquals(5, fehlerUndWarnungen.size());

			assertEquals(
				"Zeile 1: Fehler! \"Amiera; Maria;Kaled;2a;2\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen. Es sind mehr als 4 Angaben.",
				fehlerUndWarnungen.get(0));
			assertEquals(
				"Zeile 2: Fehler! \"Benedikt;2a;0\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen. Es sind weniger als 4 Angaben.",
				fehlerUndWarnungen.get(1));

			assertEquals("Zeile 3: Özcan;Bakir;2b;3: diese Klassenstufe gibt es nicht. Die Klassenstufe wurde auf \"2\" gesetzt.",
				fehlerUndWarnungen.get(2));

			assertEquals(
				"Zeile 5: \"Ellen;Lang;2a;2\" In Klasse 2a gibt es bereits ein Kind mit diesem Namen und dieser Klassenstufe",
				fehlerUndWarnungen.get(3));

			assertEquals(
				"Zeile 6: Thomas; Grütze;2b;2: In Klasse 2b gibt es bereits ein Kind mit diesem Namen und dieser Klassenstufe",
				fehlerUndWarnungen.get(4));

			verify(klassenService).importiereKlassen(any(), any(), anyList());
			verify(kinderService).importiereKinder(any(), any(), any());
			verify(kinderService).findWithSchulteilname(any());
			verify(klassenService).klassenZuSchuleLaden(SCHULKUERZEL, BENUTZER_UUID);
			verify(uploadRepository).updateUpload(persistenterUpload);
		}
	}

	@Nested
	class ResponseMessageTests {

		@Nested
		class EncodingUTF8Tests {

			private final String encoding = MkGatewayFileUtils.DEFAULT_ENCODING;

			@Test
			void should_getImportMessageReturnSuccess_when_keinFehlerUndKeineDublettenUndUTF8() {

				// Act
				String msg = service.getImportMessage(2, 0, 0, 0, encoding);

				// Assert
				assertEquals("Die Daten wurden erfolgreich importiert.", msg);

			}

			@Test
			void should_getImportMessageReturnNichtVollstaending_when_nurFehler() {

				// Act
				String msg = service.getImportMessage(2, 1, 0, 0, encoding);

				// Assert
				assertEquals(
					"Einige Kinder konnten nicht importiert werden. Den Fehlerreport können Sie herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert.",
					msg);

			}

			@Test
			void should_getImportMessageReturnNichtVollstaending_when_fehlerUndKlassenstufe() {

				// Act
				String msg = service.getImportMessage(2, 1, 3, 0, encoding);

				// Assert
				assertEquals(
					"Einige Kinder konnten nicht importiert werden. Den Fehlerreport können Sie herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert.",
					msg);

			}

			@Test
			void should_getImportMessageReturnNichtVollstaending_when_fehlerUndDublette() {

				// Act
				String msg = service.getImportMessage(2, 2, 0, 5, encoding);

				// Assert
				assertEquals(
					"Einige Kinder konnten nicht importiert werden. Den Fehlerreport können Sie herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert.",
					msg);

			}

			@Test
			void should_getImportMessageReturnNichtVollstaending_when_fehlerUndKlassenstufeUndDubletten() {

				// Act
				String msg = service.getImportMessage(2, 2, 5, 3, encoding);

				// Assert
				assertEquals(
					"Einige Kinder konnten nicht importiert werden. Den Fehlerreport können Sie herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert.",
					msg);

			}

			@Test
			void should_getImportMessageReturnVollstaendingMitWarnung_when_KlassenstufeUndDubletten() {

				// Act
				String msg = service.getImportMessage(2, 0, 5, 3, encoding);

				// Assert
				assertEquals(
					"Bei einigen Kindern war die Klassenstufe nicht korrekt und wurde automatisch auf 2 gesetzt. Es gab möglicherweise Doppeleinträge. Alle betroffenen Kinder wurden markiert.",
					msg);

			}

			@Test
			void should_getImportMessageReturnVollstaendingMitWarnungKlassenstufe_when_nurKlassenstufe() {

				// Act
				String msg = service.getImportMessage(2, 0, 5, 0, encoding);

				// Assert
				assertEquals(
					"Bei einigen Kindern war die Klassenstufe nicht korrekt und wurde automatisch auf 2 gesetzt.",
					msg);

			}

			@Test
			void should_getImportMessageReturnVollstaendingMitWarnungDublette_when_nurDublette() {

				// Act
				String msg = service.getImportMessage(2, 0, 0, 2, encoding);

				// Assert
				assertEquals(
					"Es gab möglicherweise Doppeleinträge. Alle betroffenen Kinder wurden markiert.",
					msg);

			}

			@Test
			void should_getImportMessageReturnImportFehlgeschlagen_when_keineKlasseImportiert() {

				// Act
				String msg = service.getImportMessage(0, 20, 0, 0, encoding);

				// Assert
				assertEquals(
					"Die Klassenliste konnte nicht importiert werden: alle Zeilen waren fehlerhaft. Bitte prüfen Sie die hochgeladene Datei. Den Fehlerreport können Sie herunterladen.",
					msg);

			}

		}

		@Nested
		class UnknownEncodingTests {

			@Test
			void should_getImportMessageReturnSuccess_when_keinFehlerUndKeineDublettenUndNichtUTF8() {

				// Act
				String msg = service.getImportMessage(2, 0, 0, 0, null);

				// Assert
				assertEquals("Die Daten wurden erfolgreich importiert. Bitte prüfen Sie, ob Umlaute korrekt angezeigt werden.",
					msg);

			}

			@Test
			void should_getImportMessageReturnNichtVollstaending_when_nurFehler() {

				// Act
				String msg = service.getImportMessage(2, 1, 0, 0, null);

				// Assert
				assertEquals(
					"Einige Kinder konnten nicht importiert werden. Den Fehlerreport können Sie herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert. Bitte prüfen Sie außerdem, ob Umlaute korrekt angezeigt werden.",
					msg);

			}

			@Test
			void should_getImportMessageReturnNichtVollstaending_when_fehlerUndKlassenstufe() {

				// Act
				String msg = service.getImportMessage(2, 1, 3, 0, null);

				// Assert
				assertEquals(
					"Einige Kinder konnten nicht importiert werden. Den Fehlerreport können Sie herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert. Bitte prüfen Sie außerdem, ob Umlaute korrekt angezeigt werden.",
					msg);

			}

			@Test
			void should_getImportMessageReturnNichtVollstaending_when_fehlerUndDublette() {

				// Act
				String msg = service.getImportMessage(2, 2, 0, 5, null);

				// Assert
				assertEquals(
					"Einige Kinder konnten nicht importiert werden. Den Fehlerreport können Sie herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert. Bitte prüfen Sie außerdem, ob Umlaute korrekt angezeigt werden.",
					msg);

			}

			@Test
			void should_getImportMessageReturnNichtVollstaending_when_fehlerUndKlassenstufeUndDubletten() {

				// Act
				String msg = service.getImportMessage(2, 2, 5, 3, null);

				// Assert
				assertEquals(
					"Einige Kinder konnten nicht importiert werden. Den Fehlerreport können Sie herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert. Bitte prüfen Sie außerdem, ob Umlaute korrekt angezeigt werden.",
					msg);

			}

			@Test
			void should_getImportMessageReturnVollstaendingMitWarnung_when_KlassenstufeUndDubletten() {

				// Act
				String msg = service.getImportMessage(2, 0, 5, 3, null);

				// Assert
				assertEquals(
					"Bei einigen Kindern war die Klassenstufe nicht korrekt und wurde automatisch auf 2 gesetzt. Es gab möglicherweise Doppeleinträge. Alle betroffenen Kinder wurden markiert. Bitte prüfen Sie außerdem, ob Umlaute korrekt angezeigt werden.",
					msg);

			}

			@Test
			void should_getImportMessageReturnVollstaendingMitWarnungKlassenstufe_when_nurKlassenstufe() {

				// Act
				String msg = service.getImportMessage(2, 0, 5, 0, null);

				// Assert
				assertEquals(
					"Bei einigen Kindern war die Klassenstufe nicht korrekt und wurde automatisch auf 2 gesetzt. Bitte prüfen Sie außerdem, ob Umlaute korrekt angezeigt werden.",
					msg);

			}

			@Test
			void should_getImportMessageReturnVollstaendingMitWarnungDublette_when_nurDublette() {

				// Act
				String msg = service.getImportMessage(2, 0, 0, 2, null);

				// Assert
				assertEquals(
					"Es gab möglicherweise Doppeleinträge. Alle betroffenen Kinder wurden markiert. Bitte prüfen Sie außerdem, ob Umlaute korrekt angezeigt werden.",
					msg);

			}

			@Test
			void should_getImportMessageReturnImportFehlgeschlagen_when_keineKlasseImportiert() {

				// Act
				String msg = service.getImportMessage(0, 20, 0, 0, null);

				// Assert
				assertEquals(
					"Die Klassenliste konnte nicht importiert werden: alle Zeilen waren fehlerhaft. Bitte prüfen Sie die hochgeladene Datei. Den Fehlerreport können Sie herunterladen.",
					msg);

			}

		}

	}

}
