// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.loesungszettel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.ZulaessigeLoesungszetteleingabe;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mkv_server_tests.TestUtils;

/**
 * LoesungszettelServiceIT
 */
public class LoesungszettelServiceIT extends AbstractLoesungszettelTest {

	@Test
	void should_loesungszettelAnlegenUndAendern_work() throws Exception {

		// Arrange
		String kindUuid = "6de0e323-f01f-424c-b57e-8201c91dd2ee";
		String veranstalterUuid = "c048e4d6-3d40-412a-8d8c-95fb3e0eb614";

		LoesungszettelAPIModel ersteRequestDaten = TestUtils.createLoesungszettelRequestDatenKlasseEinsKreuzeABC("neu", kindUuid);

		// Act 1
		ResponsePayload result = this.loesungszettelAnlegen(ersteRequestDaten, veranstalterUuid);

		// Assert
		assertTrue(result.isOk());

		LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) result.getData();
		String loesungszettelUuid = responseData.loesungszettelId();
		assertFalse("neu".equals(loesungszettelUuid));
		assertEquals(0, responseData.getVersion());

		assertEquals("6,25", responseData.punkte());
		assertEquals(1, responseData.laengeKaengurusprung());

		for (int i = 0; i < ersteRequestDaten.zeilen().size(); i++) {

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

		// Act 2
		LoesungszettelAPIModel zweiteRequestDaten = TestUtils
			.createLoesungszettelRequestDatenKlasseEinsKreuzeDEN(loesungszettelUuid, kindUuid)
			.withVersion(responseData.getVersion());

		responseData = (LoesungszettelpunkteAPIModel) this.loesungszettelAendern(zweiteRequestDaten, veranstalterUuid).getData();
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
		assertEquals(1, responseData.getVersion());

	}

}
