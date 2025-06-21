// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.loesungszettel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.OnlineLoesungszetteleingabe;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mkv_server_tests.TestUtils;

/**
 * OnlineLoesungszettelServiceIT
 */
public class OnlineLoesungszettelServiceIT extends AbstractLoesungszettelTest {

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

				assertEquals(OnlineLoesungszetteleingabe.A,
					responseZeile.eingabe(), "Fehler bei Zeile " + responseZeile.name());
			}

			if (responseZeile.name().startsWith("B")) {

				assertEquals(OnlineLoesungszetteleingabe.B,
					responseZeile.eingabe(), "Fehler bei Zeile " + responseZeile.name());
			}

			if (responseZeile.name().startsWith("C")) {

				assertEquals(OnlineLoesungszetteleingabe.C,
					responseZeile.eingabe(), "Fehler bei Zeile " + responseZeile.name());
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

				assertEquals(OnlineLoesungszetteleingabe.D,
					responseZeile.eingabe(), "Fehler bei Zeile " + responseZeile.name());
			}

			if (responseZeile.name().startsWith("B")) {

				assertEquals(OnlineLoesungszetteleingabe.E,
					responseZeile.eingabe(), "Fehler bei Zeile " + responseZeile.name());
			}

			if (responseZeile.name().startsWith("C")) {

				assertEquals(OnlineLoesungszetteleingabe.N,
					responseZeile.eingabe(), "Fehler bei Zeile " + responseZeile.name());
			}

		}

		assertEquals("10,00", responseData.punkte());
		assertEquals(1, responseData.laengeKaengurusprung());
		assertEquals(1, responseData.getVersion());

	}

}
