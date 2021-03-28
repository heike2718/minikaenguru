// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.loesungszettel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelService;
import de.egladil.web.mk_gateway.domain.loesungszettel.ZulaessigeLoesungszetteleingabe;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mkv_server_tests.AbstractIT;
import de.egladil.web.mkv_server_tests.TestUtils;

/**
 * LoesungszettelServiceTest
 */
public class LoesungszettelServiceTest extends AbstractIT {

	private LoesungszettelService loesungszettelService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		loesungszettelService = LoesungszettelService.createForIntegrationTest(entityManagerWettbewerbDB);
	}

	@Test
	void should_loesungszettelAnlegenUndAendern_work() {

		// Arrange
		String kindUuid = "6de0e323-f01f-424c-b57e-8201c91dd2ee";
		String veranstalterUuid = "c048e4d6-3d40-412a-8d8c-95fb3e0eb614";

		LoesungszettelAPIModel ersteRequestDaten = TestUtils.createLoesungszettelNeuKlasseEinsKreuzeABC(kindUuid);

		// Act 1
		ResponsePayload result = this.loesungszettelAnlegen(ersteRequestDaten, veranstalterUuid);

		// Assert
		assertTrue(result.isOk());

		LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) result.getData();
		String loesungszettelUuid = responseData.loesungszettelId();
		assertFalse("neu".equals(loesungszettelUuid));

		assertEquals("6,25", responseData.punkte());
		assertEquals(1, responseData.laengeKaengurusprung());

		for (int i = 0; i < ersteRequestDaten.zeilen().size(); i++) {

			LoesungszettelZeileAPIModel requestZeile = ersteRequestDaten.zeilen().get(i);
			LoesungszettelZeileAPIModel responseZeile = responseData.zeilen().get(i);

			assertEquals("Fehler bei Zeile " + responseZeile.name(), requestZeile.toString(), responseZeile.toString());
		}

		// Act 2
		LoesungszettelAPIModel zweiteRequestDaten = TestUtils.createLoesungszettelNeuKlasseEinsKreuzeDEN(kindUuid)
			.withUuid(loesungszettelUuid);

		responseData = this.loesungszettelAendern(zweiteRequestDaten, veranstalterUuid);
		assertEquals(loesungszettelUuid, responseData.loesungszettelId());

		for (int i = 0; i < responseData.zeilen().size(); i++) {

			LoesungszettelZeileAPIModel responseZeile = responseData.zeilen().get(i);

			if (responseZeile.name().startsWith("A")) {

				assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.D,
					responseZeile.eingabe());
			}

			if (responseZeile.name().startsWith("B")) {

				assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.E,
					responseZeile.eingabe());
			}

			if (responseZeile.name().startsWith("C")) {

				assertEquals("Fehler bei Zeile " + responseZeile.name(), ZulaessigeLoesungszetteleingabe.N,
					responseZeile.eingabe());
			}

		}

		assertEquals("10,00", responseData.punkte());
		assertEquals(1, responseData.laengeKaengurusprung());

	}

	@Test
	void should_AnlegenBeRejectedAndReturnPersistentData_when_Konkurrurierend() {

		// Arrange
		String kindUuid = "cd6201ce-b5d7-412d-b5cb-3a4f2541de7c";
		String veranstalterUuid = "c048e4d6-3d40-412a-8d8c-95fb3e0eb614";

		LoesungszettelAPIModel ersteRequestDaten = TestUtils.createLoesungszettelNeuKlasseEinsKreuzeABC(kindUuid);

		// Act
		ResponsePayload result = this.loesungszettelAnlegen(ersteRequestDaten, veranstalterUuid);

		// Assert
		assertTrue(result.isOk());

		LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) result.getData();
		String loesungszettelUuid = responseData.loesungszettelId();
		assertFalse("neu".equals(loesungszettelUuid));

		assertEquals("6,25", responseData.punkte());
		assertEquals(1, responseData.laengeKaengurusprung());

		for (int i = 0; i < ersteRequestDaten.zeilen().size(); i++) {

			LoesungszettelZeileAPIModel requestZeile = ersteRequestDaten.zeilen().get(i);
			LoesungszettelZeileAPIModel responseZeile = responseData.zeilen().get(i);

			assertEquals("Fehler bei Zeile " + responseZeile.name(), requestZeile.toString(), responseZeile.toString());
		}

		// Act 2
		LoesungszettelAPIModel zweiteRequestDaten = TestUtils.createLoesungszettelNeuKlasseEinsKreuzeDEN(kindUuid)
			.withUuid(loesungszettelUuid);

		result = this.loesungszettelAnlegen(zweiteRequestDaten, veranstalterUuid);

		MessagePayload messagePayload = result.getMessage();
		assertEquals("WARN", messagePayload.getLevel());

		responseData = (LoesungszettelpunkteAPIModel) result.getData();
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
	}

	private ResponsePayload loesungszettelAnlegen(final LoesungszettelAPIModel loesungszetteldaten, final String veranstalterUuid) {

		EntityTransaction trx = entityManagerWettbewerbDB.getTransaction();

		try {

			trx.begin();

			ResponsePayload result = loesungszettelService.loesungszettelAnlegen(loesungszetteldaten,
				new Identifier(veranstalterUuid));

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();

			throw new RuntimeException("Lösungszettel konnte nicht angelegt werden");
		}

	}

	private LoesungszettelpunkteAPIModel loesungszettelAendern(final LoesungszettelAPIModel loesungszetteldaten, final String veranstalterUuid) {

		EntityTransaction trx = entityManagerWettbewerbDB.getTransaction();

		try {

			trx.begin();

			LoesungszettelpunkteAPIModel result = loesungszettelService.loesungszettelAendern(loesungszetteldaten,
				new Identifier(veranstalterUuid));

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();

			throw new RuntimeException("Lösungszettel konnte nicht angelegt werden");
		}

	}

}
