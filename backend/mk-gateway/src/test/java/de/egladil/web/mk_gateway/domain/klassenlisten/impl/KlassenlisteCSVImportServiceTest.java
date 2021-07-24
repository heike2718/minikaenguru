// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderService;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenService;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.klassenlisten.api.KlassenlisteImportReport;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;

/**
 * KlassenlisteCSVImportServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class KlassenlisteCSVImportServiceTest {

	private static final String SCHULKUERZEL = "ZUTFG654F";

	private static final String VERANSTALTER_UUID = "9515636f-9909-45b2-9132-85e2e0af3cb4";

	private UploadKlassenlisteContext uploadKlassenlisteContext;

	private PersistenterUpload persistenterUpload;

	private List<Kind> vorhandeneKinder;

	@Mock
	private KlassenService klassenService;

	@Mock
	private KinderService kinderService;

	@InjectMocks
	private KlassenlisteCSVImportService service;

	@BeforeEach
	void setUp() {

		boolean nachnamenAlsZusatz = false;
		Sprache sprache = Sprache.de;
		String kuerzelLand = "DE-HE";

		vorhandeneKinder = new ArrayList<>();

		uploadKlassenlisteContext = new UploadKlassenlisteContext().withKuerzelLand(kuerzelLand)
			.withNachnameAlsZusatz(nachnamenAlsZusatz).withSprache(sprache);

		persistenterUpload = new PersistenterUpload();
		persistenterUpload.setUuid("klassenliste");
		persistenterUpload.setVeranstalterUuid(VERANSTALTER_UUID);
		persistenterUpload.setTeilnahmenummer(SCHULKUERZEL);
	}

	@Nested
	class ReadContentTests {

		@Test
		void should_readFileContentThrowMkGatewayException_when_FileDoesNotExist() {

			// Arrange
			String path = "/home/heike/git/minikaenguru/missing-file.csv";

			// Act + Assert
			try {

				service.readFileContent(path);
				fail("keine MkGatewayRuntimeException");
			} catch (MkGatewayRuntimeException e) {

				assertEquals("IOException beim Import einer Klassenliste", e.getMessage());
			}

		}

		@Test
		void should_readFileContent_IgnoreEmptyLines() {

			// Arrange
			String path = "/home/heike/upload/klassenlisten-testdaten/korrekt/klassenliste-mit-leerzeilen.csv";

			// Act
			List<String> lines = service.readFileContent(path);

			// Assert
			assertEquals(5, lines.size());
			assertEquals("Vorname,Nachname,Klasse,Klassenstufe", lines.get(0));
			assertEquals("Amiera,Kaled,2a,2", lines.get(1));
			assertEquals("Benedikt,Fichtenholz ,2a,0", lines.get(2));
			assertEquals("Özcan,Bakir,2b,2", lines.get(3));
			assertEquals("Thomas, Grütze,2b,2", lines.get(4));
		}
	}

	@Nested
	class ImportTests {

		@Test
		void should_importiereKlassenThrowUploadFormatException_when_keineUeberschrift() {

			// Arrange
			service.setPathUploadDir("/home/heike/upload/klassenlisten-testdaten/fehlerhaft");
			persistenterUpload.setUuid("ohne-ueberschrift");

			// Act + Assert
			try {

				service.importiereKinder(uploadKlassenlisteContext, persistenterUpload);
				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die hochgeladene Datei kann nicht verarbeitet werden. Die erste Zeile enthält nicht die Felder \"Nachname\", \"Vorname\", \"Klasse\", \"Klassenstufe\".",
					e.getMessage());
			}

		}

		@Test
		void should_importiereKlassenWork() throws IOException {

			// Arrange
			service.setPathUploadDir("/home/heike/upload/klassenlisten-testdaten/korrekt");

			List<Klasse> klassen = new ArrayList<>();
			klassen.add(new Klasse(new Identifier("uuid-2a")).withName("2a").withSchuleID(new Identifier(SCHULKUERZEL)));
			klassen.add(new Klasse(new Identifier("uuid-2b")).withName("2b").withSchuleID(new Identifier(SCHULKUERZEL)));

			List<KlasseAPIModel> klassenAPIModels = klassen.stream().map(k -> KlasseAPIModel.createFromKlasse(k))
				.collect(Collectors.toList());

			List<Kind> kinder = new ArrayList<>();
			kinder.add(
				new Kind(new Identifier("shdiqhio")).withKlasseID(new Identifier("uuid-2a")).withKlassenstufe(Klassenstufe.ZWEI)
					.withLandkuerzel("DE-HE").withNachname("Fichtenholz").withSprache(Sprache.de).withVorname("Genadi"));

			when(klassenService.importiereKlassen(any(), any(), anyList())).thenReturn(klassen);
			when(kinderService.importiereKinder(any(), any(), any(), any())).thenReturn(kinder);
			when(kinderService.findWithSchulteilname(any())).thenReturn(vorhandeneKinder);
			when(klassenService.klassenZuSchuleLaden(SCHULKUERZEL, VERANSTALTER_UUID)).thenReturn(klassenAPIModels);

			// Act
			ResponsePayload responsePayload = service.importiereKinder(uploadKlassenlisteContext, persistenterUpload);

			MessagePayload messagePayload = responsePayload.getMessage();
			assertTrue(messagePayload.isOk());
			assertEquals("Die Daten wurden erfolgreich importiert.", messagePayload.getMessage());

			assertNotNull(responsePayload.getData());

			KlassenlisteImportReport report = (KlassenlisteImportReport) responsePayload.getData();
			assertEquals(2, report.getKlassen().size());
			assertEquals(1, report.getAnzahlKinderImportiert());
			assertEquals(0L, report.getAnzahlDubletten());
			assertEquals(0L, report.getAnzahlKlassenstufeUnklar());
			assertEquals(0, report.getAnzahlNichtImportiert());
			assertNull(report.getUuidImportReport());
			List<String> fehlermeldungen = report.getNichtImportierteZeilen();
			assertEquals(0, fehlermeldungen.size());
		}

		@Test
		void should_importiereKlassenWork_withFehlern() throws IOException {

			// Arrange
			service.setPathUploadDir("/home/heike/upload/klassenlisten-testdaten/fehlerhaft");
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

			when(klassenService.importiereKlassen(any(), any(), anyList())).thenReturn(klassen);
			when(kinderService.importiereKinder(any(), any(), any(), any())).thenReturn(kinder);
			when(kinderService.findWithSchulteilname(any())).thenReturn(vorhandeneKinder);
			when(klassenService.klassenZuSchuleLaden(SCHULKUERZEL, VERANSTALTER_UUID)).thenReturn(klassenAPIModels);

			// Act
			ResponsePayload responsePayload = service.importiereKinder(uploadKlassenlisteContext, persistenterUpload);

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Einige Kinder konnten nicht importiert werden. Einen Fehlerreport können Sie mit dem Link herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert.",
				messagePayload.getMessage());

			assertNotNull(responsePayload.getData());

			KlassenlisteImportReport report = (KlassenlisteImportReport) responsePayload.getData();
			assertEquals(2, report.getKlassen().size());
			assertEquals(1, report.getAnzahlKinderImportiert());
			assertEquals(2L, report.getAnzahlDubletten());
			assertEquals(1L, report.getAnzahlKlassenstufeUnklar());
			assertEquals(2, report.getAnzahlNichtImportiert());
			assertEquals("mit-ueberschrift-alle-anderen-faelle", report.getUuidImportReport());
			List<String> fehlermeldungen = report.getNichtImportierteZeilen();
			assertEquals(2, fehlermeldungen.size());

			assertEquals(
				"Fehler! Zeile \"Amiera, Maria,Kaled,2a,2\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen.",
				fehlermeldungen.get(0));
			assertEquals(
				"Fehler! Zeile \"Benedikt,2a,0\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen.",
				fehlermeldungen.get(1));
		}

	}

	@Nested
	class ResponseMessageTests {

		@Test
		void should_getImportMessageReturnSuccess_when_keinFehlerUndKeineDubletten() {

			// Act
			String msg = service.getImportMessage(0, 0, 0);

			// Assert
			assertEquals("Die Daten wurden erfolgreich importiert.", msg);

		}

		@Test
		void should_getImportMessageReturnNichtVollstaending_when_nurFehler() {

			// Act
			String msg = service.getImportMessage(1, 0, 0);

			// Assert
			assertEquals(
				"Einige Kinder konnten nicht importiert werden. Einen Fehlerreport können Sie mit dem Link herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert.",
				msg);

		}

		@Test
		void should_getImportMessageReturnNichtVollstaending_when_fehlerUndKlassenstufe() {

			// Act
			String msg = service.getImportMessage(1, 3, 0);

			// Assert
			assertEquals(
				"Einige Kinder konnten nicht importiert werden. Einen Fehlerreport können Sie mit dem Link herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert.",
				msg);

		}

		@Test
		void should_getImportMessageReturnNichtVollstaending_when_fehlerUndDublette() {

			// Act
			String msg = service.getImportMessage(2, 0, 5);

			// Assert
			assertEquals(
				"Einige Kinder konnten nicht importiert werden. Einen Fehlerreport können Sie mit dem Link herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert.",
				msg);

		}

		@Test
		void should_getImportMessageReturnNichtVollstaending_when_fehlerUndKlassenstufeUndDubletten() {

			// Act
			String msg = service.getImportMessage(2, 5, 3);

			// Assert
			assertEquals(
				"Einige Kinder konnten nicht importiert werden. Einen Fehlerreport können Sie mit dem Link herunterladen. Kinder mit unklarer Klassenstufe oder Doppeleinträge wurden markiert.",
				msg);

		}

		@Test
		void should_getImportMessageReturnVollstaendingMitWarnung_when_KlassenstufeUndDubletten() {

			// Act
			String msg = service.getImportMessage(0, 5, 3);

			// Assert
			assertEquals(
				"Bei einigen Kindern war die Klassenstufe nicht korrekt und wurde automatisch auf 2 gesetzt. Es gab möglicherweise Doppeleinträge. Alle betroffenen Kinder wurden markiert.",
				msg);

		}

		@Test
		void should_getImportMessageReturnVollstaendingMitWarnungKlassenstufe_when_nurKlassenstufe() {

			// Act
			String msg = service.getImportMessage(0, 5, 0);

			// Assert
			assertEquals(
				"Bei einigen Kindern war die Klassenstufe nicht korrekt und wurde automatisch auf 2 gesetzt.",
				msg);

		}

		@Test
		void should_getImportMessageReturnVollstaendingMitWarnungDublette_when_nurDublette() {

			// Act
			String msg = service.getImportMessage(0, 0, 2);

			// Assert
			assertEquals(
				"Es gab möglicherweise Doppeleinträge. Alle betroffenen Kinder wurden markiert.",
				msg);

		}

	}

}
