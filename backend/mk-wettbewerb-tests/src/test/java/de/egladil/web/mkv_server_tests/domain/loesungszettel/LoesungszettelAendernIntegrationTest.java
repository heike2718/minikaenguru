// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.loesungszettel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.error.ConcurrentModificationType;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.ZulaessigeLoesungszetteleingabe;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mkv_server_tests.TestUtils;

/**
 * LoesungszettelAendernIntegrationTest
 */
public class LoesungszettelAendernIntegrationTest extends AbstractLoesungszettelTest {

	private static final Identifier VERANSTALTER_VC91WP8L_ID = new Identifier("eee4dcf4-decf-4d7f-89cd-ea2516122320");

	private static final Identifier VERANSTALTER_ZSH712H9_ID = new Identifier("4e060c0f-ccaa-40b6-965f-3267f84df234");

	private static final Identifier VERANSTALTER_KI7PLSUB_ID = new Identifier("412b67dc-132f-465a-a3c3-468269e866cb");

	@Nested
	class KindExistiertNichtTests {

		@Test
		@DisplayName("1) kind und loesungszettel existieren nicht => 422: konkurrierend gelöscht")
		void should_loesungszettelAendernThrowNotFoundException_when_noKindPresent() {

			// Arrange
			Identifier veranstalterID = VERANSTALTER_ZSH712H9_ID;
			String loesungszettelUuid = "yyyyyyyy-7390-451d-8dc1-ea6e6db0466b";
			String kindUuid = "zzzzzzzz-59c3-4d41-a9dd-acc7d79ad96f";

			Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue(optKind.isEmpty());

			Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
			assertTrue(optLoesungszettel.isEmpty());

			LoesungszettelAPIModel requestDaten = TestUtils
				.createLoesungszettelRequestDatenKlasseZWEIKreuzeABC(loesungszettelUuid, kindUuid);

			// Act
			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) loesungszettelAendern(requestDaten,
				veranstalterID.identifier()).getData();

			// Assert
			assertEquals(ConcurrentModificationType.DETETED, result.getConcurrentModificationType());
			assertEquals(loesungszettelUuid, result.loesungszettelId());

		}

		@Test
		@DisplayName("2) loesungszettel existiert: loesungszettel.jahr == vorjahr Jahr, loesungszettel.kindID != null => Abbruch und 422")
		void should_loesungszettelAendernTriggerThrowInvalidInputException_when_kindAbsentAndLoesungszettelAusVorjahr() {

			// Arrange
			Identifier veranstalterID = VERANSTALTER_ZSH712H9_ID;
			String loesungszettelUuid = "fbf0283b-6038-42e1-bfc0-d0bfc3e30214";
			String kindUuid = "feb0b1dd-59c3-4d41-a9dd-acc7d79ad96f";

			Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue(optKind.isPresent());

			Kind clumsy = optKind.get();
			assertEquals("9b14ce6e-7390-451d-8dc1-ea6e6db0466b", clumsy.loesungszettelID().identifier());

			Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
			assertTrue("DB muss zurückgesetzt werden", optLoesungszettel.isPresent());

			Loesungszettel loesungszettel = optLoesungszettel.get();
			assertEquals("2017", loesungszettel.teilnahmeIdentifier().wettbewerbID());
			assertEquals("f,r,f,r,f,f,f,f,r,r,f,r,r,r,f", loesungszettel.rohdaten().nutzereingabe());

			LoesungszettelAPIModel requestDaten = TestUtils
				.createLoesungszettelRequestDatenKlasseZWEIKreuzeABC(loesungszettelUuid, kindUuid)
				.withUuid(loesungszettelUuid);

			// Act
			try {

				loesungszettelAendern(requestDaten, veranstalterID.identifier());
				fail("keine InvalidInputException");
			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();
				MessagePayload messagePayload = responsePayload.getMessage();
				assertEquals("ERROR", messagePayload.getLevel());
				assertEquals(
					"Der Lösungszettel konnte leider nicht gespeichert werden: es gibt inkonsistente Daten in der Datenbank. Bitte wenden Sie sich per Mail an info@egladil.de.",
					messagePayload.getMessage());

				optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));

				loesungszettel = optLoesungszettel.get();
				assertEquals("2017", loesungszettel.teilnahmeIdentifier().wettbewerbID());
				assertEquals("f,r,f,r,f,f,f,f,r,r,f,r,r,r,f", loesungszettel.rohdaten().nutzereingabe());
			}
		}

		@Test
		@DisplayName("3) loesungszettel existiert: loesungszettel.jahr == aktuelles Jahr, loesungszettel.kindID != null => concurrent modification")
		void should_loesungszettelAendernTriggerDeleteEventAndThrowNotFoundException_when_kindAbsentAndLoesungszettelPresent() {

			Identifier veranstalterID = VERANSTALTER_VC91WP8L_ID;
			String loesungszettelUuid = "ee3b8ad0-9538-4803-bc41-217281c67e1d";
			String kindUuid = "7eaa4bf6-ee85-4047-8c8d-b82aa3fd849y";

			Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue(optKind.isEmpty());

			Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
			assertTrue("DB muss zurückgesetzt werden", optLoesungszettel.isPresent());

			Loesungszettel loesungszettel = optLoesungszettel.get();
			assertEquals("DB muss zurückgesetzt werden", new Identifier(kindUuid), loesungszettel.kindID());

			LoesungszettelAPIModel requestDaten = TestUtils
				.createLoesungszettelRequestDatenKlasseZWEIKreuzeABC(loesungszettelUuid, kindUuid)
				.withUuid(loesungszettelUuid);

			// Act
			ResponsePayload responsePayload = loesungszettelAendern(requestDaten, veranstalterID.identifier());
			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals("Das Kind wurde in der Zwischenzeit gelöscht. Bitte klären Sie dies im Kollegium.",
				messagePayload.getMessage());

			assertEquals(ConcurrentModificationType.DETETED, result.getConcurrentModificationType());
			assertEquals(loesungszettelUuid, result.loesungszettelId());
			assertTrue(loesungszettelRepository.ofID(veranstalterID).isEmpty());
		}

	}

	@Nested
	class KindExistiertLoesungszettelExistiertNichtTests {

		@Test
		@DisplayName("1) kind.lzID != null, anderer loesungszettel mit kind.lzId existiert nicht => anlegen")
		void should_loesungszettelAendernSwitchToAnlegen_when_kindPresentLoesungszettelAbsent_andLoesungszettelMitkindLzIDAbsent() {

			// Arrange , zu ändernder lösungszettel=
			String loesungszettelUuid = "4d4ce7d3-2027-47d4-93d8-4f38af456cbf";
			String kindUuid = "b85c8e52-ba6d-4069-9557-970ab46cd2ec";

			Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue(optKind.isPresent());

			Kind benjamin = optKind.get();
			assertEquals(loesungszettelUuid, benjamin.loesungszettelID().identifier());

			Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
			assertTrue("DB muss zurückgesetzt werden", optLoesungszettel.isEmpty());

			LoesungszettelAPIModel requestDaten = TestUtils
				.createLoesungszettelRequestDatenKlasseEinsKreuzeABC(loesungszettelUuid, kindUuid)
				.withUuid(loesungszettelUuid);

			// Act
			LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) loesungszettelAendern(requestDaten,
				VERANSTALTER_VC91WP8L_ID.identifier()).getData();

			// Assert
			for (int i = 0; i < responseData.zeilen().size(); i++) {

				LoesungszettelZeileAPIModel responseZeile = responseData.zeilen().get(i);

				if (responseZeile.name().startsWith("A")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.A,
						responseZeile.eingabe());
				}

				if (responseZeile.name().startsWith("B")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.B,
						responseZeile.eingabe());
				}

				if (responseZeile.name().startsWith("C")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.C,
						responseZeile.eingabe());
				}

			}

			String neueLoesungszettelUuid = responseData.loesungszettelId();

			optLoesungszettel = loesungszettelRepository.ofID(new Identifier(neueLoesungszettelUuid));
			assertFalse(optLoesungszettel.isEmpty());
			Loesungszettel loesungszettel = optLoesungszettel.get();
			assertEquals("AAAABBBBCCCC", loesungszettel.rohdaten().nutzereingabe());
			assertEquals(625, loesungszettel.punkte());

			optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue(optKind.isPresent());

			benjamin = optKind.get();
			assertEquals(neueLoesungszettelUuid, benjamin.loesungszettelID().identifier());

		}

		@Test
		@DisplayName("2) kind.lzID null, anderer loesungszettel existiert nicht => anlegen")
		void should_loesungszettelAendernSwitchToAnlegen_when_kindPresentLoesungszettelAbsent_andKindOhneLoesungszettelreferenz() {

			// Arrange
			String kindUuid = "544c092b-5549-4d77-9edc-be69d8d9531e";
			String loesungszettelUuid = "ztzeusgt-5549-4d77-9edc-be69d8d9531e";

			Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue(optKind.isPresent());

			Kind xaver = optKind.get();
			assertNull(xaver.loesungszettelID());

			Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository
				.findLoesungszettelWithKindID(new Identifier(kindUuid));
			assertTrue("DB muss zurückgesetzt werden", optLoesungszettel.isEmpty());

			LoesungszettelAPIModel requestDaten = TestUtils
				.createLoesungszettelRequestDatenKlasseEinsKreuzeABC(loesungszettelUuid, kindUuid)
				.withUuid(loesungszettelUuid);

			// Act
			LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) loesungszettelAendern(requestDaten,
				VERANSTALTER_VC91WP8L_ID.identifier()).getData();

			// Assert
			for (int i = 0; i < responseData.zeilen().size(); i++) {

				LoesungszettelZeileAPIModel responseZeile = responseData.zeilen().get(i);

				if (responseZeile.name().startsWith("A")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.A,
						responseZeile.eingabe());
				}

				if (responseZeile.name().startsWith("B")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.B,
						responseZeile.eingabe());
				}

				if (responseZeile.name().startsWith("C")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.C,
						responseZeile.eingabe());
				}

			}

			String neueLoesungszettelUuid = responseData.loesungszettelId();

			optLoesungszettel = loesungszettelRepository.ofID(new Identifier(neueLoesungszettelUuid));
			assertFalse(optLoesungszettel.isEmpty());
			Loesungszettel loesungszettel = optLoesungszettel.get();
			assertEquals("AAAABBBBCCCC", loesungszettel.rohdaten().nutzereingabe());
			assertEquals(625, loesungszettel.punkte());

			optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue(optKind.isPresent());

			xaver = optKind.get();
			assertEquals(neueLoesungszettelUuid, xaver.loesungszettelID().identifier());
		}

		@Test
		@DisplayName("3) kindLz != null, referencedLZ null, requestedLz == kindLz, exists lz with kindId = kind => existing loesungszettel ändern und kind.lzID ändern")
		void should_loesungszettelAendernAendertReferenziertenLoesungszettel_when_referencedLoesungszettelAbsentRequestLoesungszettelAbsentLoesungszettelMitKindIDExists() {

			String loesungszettelExistingLoesungszettelUuid = "54538c66-9401-4407-a0f8-bc328d48962b";
			String kindUuid = "3ed7eb57-a838-4398-977b-b0ff347a8dbb";

			String requestLoesungszettelUuid = "64f5f7a2-157d-45d1-956b-6be0319934a4";

			Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue(optKind.isPresent());

			Kind astrid = optKind.get();
			assertEquals("DB muss zurückgesetzt werden", requestLoesungszettelUuid, astrid.loesungszettelID().identifier());

			Optional<Loesungszettel> referenzierterLoesungszettel = loesungszettelRepository
				.ofID(new Identifier(requestLoesungszettelUuid));
			assertTrue(referenzierterLoesungszettel.isEmpty());

			Optional<Loesungszettel> optExistierenderLoesungszettel = loesungszettelRepository
				.ofID(new Identifier(loesungszettelExistingLoesungszettelUuid));
			assertTrue("DB muss zurückgesetzt werden", optExistierenderLoesungszettel.isPresent());
			assertEquals(kindUuid, optExistierenderLoesungszettel.get().kindID().identifier());
			assertEquals("NNNNNNNNNNNA", optExistierenderLoesungszettel.get().rohdaten().nutzereingabe());

			LoesungszettelAPIModel requestDaten = TestUtils
				.createLoesungszettelRequestDatenKlasseEinsKreuzeABC(requestLoesungszettelUuid, kindUuid)
				.withUuid(requestLoesungszettelUuid);

			// Act
			LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) loesungszettelAendern(requestDaten,
				VERANSTALTER_VC91WP8L_ID.identifier()).getData();

			// Assert
			for (int i = 0; i < responseData.zeilen().size(); i++) {

				LoesungszettelZeileAPIModel responseZeile = responseData.zeilen().get(i);

				if (responseZeile.name().startsWith("A")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.A,
						responseZeile.eingabe());
				}

				if (responseZeile.name().startsWith("B")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.B,
						responseZeile.eingabe());
				}

				if (responseZeile.name().startsWith("C")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.C,
						responseZeile.eingabe());
				}

			}

			String responseLoesungszettelUuid = responseData.loesungszettelId();
			assertFalse(loesungszettelExistingLoesungszettelUuid.equals(responseLoesungszettelUuid));

			Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository
				.ofID(new Identifier(loesungszettelExistingLoesungszettelUuid));
			assertTrue(optLoesungszettel.isEmpty());

			Optional<Loesungszettel> optNeuer = loesungszettelRepository.ofID(new Identifier(responseLoesungszettelUuid));

			assertTrue(optNeuer.isPresent());

			Loesungszettel loesungszettel = optNeuer.get();
			assertEquals("AAAABBBBCCCC", loesungszettel.rohdaten().nutzereingabe());
			assertEquals(625, loesungszettel.punkte());

			optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue(optKind.isPresent());

			astrid = optKind.get();
			assertEquals(loesungszettel.identifier(), astrid.loesungszettelID());
		}
	}

	@Nested
	class KindUndLoesungszettelExistierenTests {

		@Test
		@DisplayName("2) kind.lzID != null, anderer loesungszettel mit kind.lzId existiert, lz.kindID != kindID => Abbruch mit 422 inkonsistente Daten")
		void should_loesungszettelAendernThrowInvalidInputException_when_kindPresentLoesungszettelAbsent_andKindMitReferenzAufAnderenLZ() {

			String loesungszettelUuid = "6a8f2c46-1a90-4af4-afa4-d3bc5a3c22f1";
			String kindUuid = "a910ba37-f7c1-4cee-9f6e-b374e0773a54";

			String anderesKindUuid = "05162d47-3539-43af-8e24-ff3378719e36";

			Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue(optKind.isPresent());

			Kind inge = optKind.get();
			assertEquals("DB muss zurückgesetzt werden", loesungszettelUuid, inge.loesungszettelID().identifier());

			Optional<Loesungszettel> optExistierenderLoesungszettel = loesungszettelRepository
				.ofID(new Identifier(loesungszettelUuid));

			assertTrue("DB muss zurückgesetzt werden", optExistierenderLoesungszettel.isPresent());
			assertEquals("DB muss zurückgesetzt werden", anderesKindUuid,
				optExistierenderLoesungszettel.get().kindID().identifier());
			assertEquals("EDABDBAEABNE", optExistierenderLoesungszettel.get().rohdaten().nutzereingabe());

			LoesungszettelAPIModel requestDaten = TestUtils
				.createLoesungszettelRequestDatenKlasseZWEIKreuzeABC(loesungszettelUuid, kindUuid);

			// Act
			try {

				loesungszettelAendern(requestDaten, VERANSTALTER_VC91WP8L_ID.identifier());

			} catch (InvalidInputException e) {

				// Assert
				ResponsePayload responsePayload = e.getResponsePayload();

				MessagePayload messagePayload = responsePayload.getMessage();
				assertEquals("ERROR", messagePayload.getLevel());
				assertEquals(
					"Der Lösungszettel konnte leider nicht gespeichert werden: es gibt inkonsistente Daten in der Datenbank. Bitte wenden Sie sich per Mail an info@egladil.de.",
					messagePayload.getMessage());

				assertNull(responsePayload.getData());

				Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository
					.findLoesungszettelWithKindID(new Identifier(anderesKindUuid));

				assertTrue(optLoesungszettel.isPresent());
				Loesungszettel loesungszettel = optLoesungszettel.get();
				assertEquals(anderesKindUuid, loesungszettel.kindID().identifier());
				assertEquals("EDABDBAEABNE", loesungszettel.rohdaten().nutzereingabe());
				assertEquals(1250, loesungszettel.punkte());

				optKind = kinderRepository.ofId(new Identifier(kindUuid));
				assertTrue(optKind.isPresent());

				inge = optKind.get();
				assertEquals(loesungszettelUuid, inge.loesungszettelID().identifier());
			}
		}

		@Test
		@DisplayName("8) konkurrierendes update wird verhindert ")
		void should_loesungszettelAendernReturnConcurrentModification_when_actVerisionGreaterThanReadVersion() {

			String loesungszettelUuid = "3567d656-f788-4fec-bb71-be6c793ee961";
			String kindUuid = "08cf931e-961e-461a-94a2-6e44965440a5";
			String expectedNutzereingabe = "BADNACACBENNNNC";

			Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue(optKind.isPresent());

			Optional<Loesungszettel> optExistierenderLoesungszettel = loesungszettelRepository
				.ofID(new Identifier(loesungszettelUuid));

			assertTrue("DB muss zurückgesetzt werden", optExistierenderLoesungszettel.isPresent());
			assertEquals(expectedNutzereingabe, optExistierenderLoesungszettel.get().rohdaten().nutzereingabe());

			LoesungszettelAPIModel requestDaten = TestUtils
				.createLoesungszettelRequestDatenKlasseZWEIKreuzeABC(loesungszettelUuid, kindUuid).withVersion(4);

			// Act
			ResponsePayload responsePayload = loesungszettelAendern(requestDaten, VERANSTALTER_KI7PLSUB_ID.identifier());
			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Ein Lösungszettel für dieses Kind wurde bereits durch eine andere Person gespeichert. Bitte prüfen Sie die neuen Daten. Punkte 48,25, Länge Kängurusprung 6.",
				messagePayload.getMessage());

			assertEquals(5, result.getVersion());
			assertEquals(ConcurrentModificationType.UPDATED, result.getConcurrentModificationType());

			Loesungszettel alter = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid)).get();
			assertEquals(5, alter.version());
			assertEquals(expectedNutzereingabe, alter.rohdaten().nutzereingabe());
		}

		@Test
		@DisplayName("jemand hat loesungszettelreferenz aus kind gelöscht")
		void should_aendernKorrigiertReferenzGeloescht() {

			// Arrange
			String loesungszettelUuid = "5ffefc14-755c-4eab-98d6-4f1489e2e936";
			String kindUuid = "41825d54-de55-4424-b6e3-4dec8b01b7e9";

			Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
			assertTrue("DB muss zurückgesetzt werden", optKind.isPresent());

			Kind frank = optKind.get();
			assertNull("DB muss zurückgesetzt werden", frank.loesungszettelID());

			Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
			assertTrue("DB muss zurückgesetzt werden", optLoesungszettel.isPresent());

			Loesungszettel loesungszettel = optLoesungszettel.get();
			assertEquals("DB muss zurückgesetzt werden", new Identifier(kindUuid), loesungszettel.kindID());

			LoesungszettelAPIModel requestDaten = TestUtils
				.createLoesungszettelRequestDatenKlasseEinsKreuzeABC(loesungszettelUuid, kindUuid).withVersion(0);

			// Act
			ResponsePayload responsePayload = loesungszettelAendern(requestDaten, VERANSTALTER_VC91WP8L_ID.identifier());

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
				messagePayload.getMessage());

			LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			// Assert
			for (int i = 0; i < responseData.zeilen().size(); i++) {

				LoesungszettelZeileAPIModel responseZeile = responseData.zeilen().get(i);

				if (responseZeile.name().startsWith("A")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.A,
						responseZeile.eingabe());
				}

				if (responseZeile.name().startsWith("B")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.B,
						responseZeile.eingabe());
				}

				if (responseZeile.name().startsWith("C")) {

					assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.C,
						responseZeile.eingabe());
				}

			}

			assertEquals(1, responseData.getVersion());
			assertNull(responseData.getConcurrentModificationType());

			Kind geaendertesKind = kinderRepository.ofId(new Identifier(kindUuid)).get();
			assertEquals(loesungszettelUuid, geaendertesKind.loesungszettelID().identifier());

			Loesungszettel geaenderterLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid)).get();
			assertEquals(1, geaenderterLoesungszettel.version());
			assertEquals("AAAABBBBCCCC", geaenderterLoesungszettel.rohdaten().nutzereingabe());
		}

	}

}
