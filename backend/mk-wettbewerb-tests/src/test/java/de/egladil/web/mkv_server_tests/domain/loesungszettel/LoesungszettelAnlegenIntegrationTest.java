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

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.loesungszettel.ZulaessigeLoesungszetteleingabe;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mkv_server_tests.TestUtils;

/**
 * LoesungszettelAnlegenIntegrationTest
 */
public class LoesungszettelAnlegenIntegrationTest extends AbstractLoesungszettelTest {

	private static final String LOESUNGSZETTEL_REQUEST_UUID = "neu";

	private static final String VERANSTALTER_UUID = "eee4dcf4-decf-4d7f-89cd-ea2516122320";

	@Test
	@DisplayName("1) Veranstalter nicht berechtigt => 401")
	void should_loesungszettelAnlegenThrowAccessDeniedException_when_VeranstalterNichtBerechtigt() {

		// Arrange
		String kindUuid = "907136fe-240d-4a4b-a525-5c643cb5f0f3";
		String veranstalterUuid = "412b67dc-132f-465a-a3c3-468269e866cb";

		LoesungszettelAPIModel requestDaten = TestUtils
			.createLoesungszettelRequestDatenKlasseZWEIKreuzeABC(LOESUNGSZETTEL_REQUEST_UUID, kindUuid)
			.withUuid(LOESUNGSZETTEL_REQUEST_UUID);

		// Act
		try {

			this.loesungszettelAnlegen(requestDaten, veranstalterUuid);
			fail("AccessDeniedException");
		} catch (AccessDeniedException e) {

			assertNull(e.getMessage());
		}
	}

	@Test
	@DisplayName("2) kind mit kindID existiert nicht => 404")
	void should_loesungszettelAnlegenThrowNotFoundException_when_kindExistiertNicht() {

		// Arrange
		String kindUuid = "ghi768-240d-4a4b-a525-5c643cb5f0f3";

		LoesungszettelAPIModel requestDaten = TestUtils
			.createLoesungszettelRequestDatenKlasseZWEIKreuzeABC(LOESUNGSZETTEL_REQUEST_UUID, kindUuid)
			.withUuid(LOESUNGSZETTEL_REQUEST_UUID);

		// Act
		try {

			this.loesungszettelAnlegen(requestDaten, VERANSTALTER_UUID);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

		}
	}

	@Test
	@DisplayName("3) kind mit kindID existiert, hat keine lzID => anlegen")
	void should_loesungszettelAnlegenWork_when_kindExistiertUndLoesungszettelExistiertNicht() {

		// Arrange
		String kindUuid = "907136fe-240d-4a4b-a525-5c643cb5f0f3";

		Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
		assertTrue(optKind.isPresent());

		Kind karla = optKind.get();
		assertNull("DB muss zurückgesetzt werden", karla.loesungszettelID());

		LoesungszettelAPIModel requestDaten = TestUtils
			.createLoesungszettelRequestDatenKlasseZWEIKreuzeABC(LOESUNGSZETTEL_REQUEST_UUID, kindUuid)
			.withUuid(LOESUNGSZETTEL_REQUEST_UUID);

		// Act
		ResponsePayload responsePayload = this.loesungszettelAnlegen(requestDaten, VERANSTALTER_UUID);

		// Assert
		assertTrue(responsePayload.isOk());
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("Der Lösungszettel wurde erfolgreich gespeichert: Punkte 25,00, Länge Kängurusprung 1.",
			messagePayload.getMessage());

		LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) responsePayload.getData();

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

		Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(new Identifier(neueLoesungszettelUuid));
		assertFalse(optLoesungszettel.isEmpty());
		assertEquals("AAAAABBBBBCCCCC", optLoesungszettel.get().rohdaten().nutzereingabe());

		optKind = kinderRepository.ofId(new Identifier(kindUuid));
		assertTrue(optKind.isPresent());

		karla = optKind.get();
		assertEquals(neueLoesungszettelUuid, karla.loesungszettelID().identifier());

	}

	@Test
	@DisplayName("4) Referenzen verwirrt: kind mit kindID existiert, hat lzID, LZ mit lzID existiert und hat andere kindID => Abbruch mit 422")
	void should_loesungszettelAnlegenInvalidInputException_when_datenInkonsistent() {

		// Arrange
		String kindUuid = "a2d11023-582e-4e53-97a7-b54a8bb5f711";
		String loesungszettelUuid = "d0e6644c-8202-49cf-b434-d69225b0556c";

		Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
		assertTrue(optKind.isPresent());

		Kind johann = optKind.get();
		assertEquals("DB muss zurückgesetzt werden", loesungszettelUuid, johann.loesungszettelID().identifier());

		Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
		assertTrue("DB muss zurückgesetzt werden", optLoesungszettel.isPresent());

		assertFalse(kindUuid.equals(optLoesungszettel.get().kindID().identifier()));

		LoesungszettelAPIModel requestDaten = TestUtils
			.createLoesungszettelRequestDatenKlasseZWEIKreuzeABC(LOESUNGSZETTEL_REQUEST_UUID, kindUuid)
			.withUuid(LOESUNGSZETTEL_REQUEST_UUID);

		// Act
		try {

			this.loesungszettelAnlegen(requestDaten, VERANSTALTER_UUID);
			fail("keine InvalidInputException");
		} catch (InvalidInputException e) {

			ResponsePayload responsePayload = e.getResponsePayload();
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals(
				"Der Lösungszettel konnte leider nicht gespeichert werden: es gibt inkonsistente Daten in der Datenbank. Bitte wenden Sie sich per Mail an info@egladil.de.",
				messagePayload.getMessage());

		}
	}

	@Test
	@DisplayName("5) kind mit kindID existiert, hat lzID, LZ mit lzID existiert und hat gleiche kindID => concurrent insert")
	void should_AnlegenBeRejectedAndReturnPersistentData_when_Konkurrurierend() throws Exception {

		// Arrange
		String kindUuid = "09151448-02fa-4072-b95c-d8ded1baea6d";
		String loesungszettelUuid = "0bf5e030-94c9-4d33-b39c-b5d52cbd74d2";

		Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
		assertTrue("DB muss zurückgesetzt werden", optLoesungszettel.isPresent());

		Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
		assertTrue("DB muss zurückgesetzt werden", optKind.isPresent());

		assertEquals("DB muss zurückgesetzt werden", optKind.get().identifier(), optLoesungszettel.get().kindID());

		LoesungszettelRohdaten rohdaten = optLoesungszettel.get().rohdaten();
		assertEquals("AAAABBBBCCCC", rohdaten.nutzereingabe());

		LoesungszettelAPIModel zweiteRequestDaten = TestUtils
			.createLoesungszettelRequestDatenKlasseEinsKreuzeDEN(LOESUNGSZETTEL_REQUEST_UUID, kindUuid)
			.withUuid(loesungszettelUuid);

		// Act
		ResponsePayload result = this.loesungszettelAnlegen(zweiteRequestDaten, VERANSTALTER_UUID);

		MessagePayload messagePayload = result.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Ein Lösungszettel für dieses Kind wurde bereits durch eine andere Person gespeichert. Bitte prüfen Sie die neuen Daten. Punkte 6,25, Länge Kängurusprung 1.",
			messagePayload.getMessage());

		LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) result.getData();
		assertEquals(loesungszettelUuid, responseData.loesungszettelId());

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

		rohdaten = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid)).get().rohdaten();
		assertEquals("AAAABBBBCCCC", rohdaten.nutzereingabe());
	}

	@Test
	@DisplayName("6) kind mit kindID existiert, hat lzID, LZ mit lzID existiert nicht => anlegen")
	void should_loesungszettelAnlegenInsertTheLoesungszettel_when_kindLoesungszettelIdNotNullButLoesungszettelAbsent() {

		// Arrange
		String kindUuid = "eeb30bfe-54fd-4181-83a3-7efe7c4599a4";
		String loesungszettelUuid = "1358724b-2685-41df-b2ec-40e77aea874d";

		Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
		assertFalse("DB muss zurückgesetzt werden", optLoesungszettel.isPresent());

		Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
		assertTrue(optKind.isPresent());

		Kind wilma = optKind.get();
		assertEquals("DB muss zurückgesetzt werden", loesungszettelUuid, wilma.loesungszettelID().identifier());

		LoesungszettelAPIModel requestDaten = TestUtils
			.createLoesungszettelRequestDatenKlasseEinsKreuzeABC(LOESUNGSZETTEL_REQUEST_UUID, kindUuid)
			.withUuid(loesungszettelUuid);

		// Act
		ResponsePayload responsePayload = this.loesungszettelAnlegen(requestDaten, VERANSTALTER_UUID);

		// Assert
		assertTrue(responsePayload.isOk());
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
			messagePayload.getMessage());

		LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) responsePayload.getData();

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
		assertFalse(loesungszettelUuid.equals(neueLoesungszettelUuid));

		optLoesungszettel = loesungszettelRepository.ofID(new Identifier(neueLoesungszettelUuid));
		assertFalse(optLoesungszettel.isEmpty());
		assertEquals("AAAABBBBCCCC", optLoesungszettel.get().rohdaten().nutzereingabe());

		optKind = kinderRepository.ofId(new Identifier(kindUuid));
		assertTrue(optKind.isPresent());

		wilma = optKind.get();
		assertEquals(neueLoesungszettelUuid, wilma.loesungszettelID().identifier());

	}

}
