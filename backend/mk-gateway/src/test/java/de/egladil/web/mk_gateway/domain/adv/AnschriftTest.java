// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.VertragAdvAPIModel;

/**
 * AnschriftTest
 */
public class AnschriftTest {

	@Test
	void should_create_initAllAttributes() {

		// Arrange
		String schulname = "Hijahsdho";
		String plz = "76545";
		String ort = "Ggagdu";
		String strasse = "Ggu-Htfzu-Straße";
		String hausnummer = "13-15";

		String schulkuerzel = "ASDERS";

		VertragAdvAPIModel apiModel = new VertragAdvAPIModel()
			.withHausnummer(hausnummer)
			.withOrt(ort)
			.withPlz(plz)
			.withSchulkuerzel(schulkuerzel)
			.withSchulname(schulname)
			.withStrasse(strasse);

		Map<String, Object> schuleKatalogeMap = new HashMap<>();

		schuleKatalogeMap.put("kuerzel", schulkuerzel);
		schuleKatalogeMap.put("name", "Schule 98765");
		schuleKatalogeMap.put("ort", ort);
		schuleKatalogeMap.put("land", "Hessen");
		schuleKatalogeMap.put("kuerzelLand", "DE-HE");

		SchuleAPIModel schuleAPIModel = SchuleAPIModel.withAttributes(schuleKatalogeMap);

		// Act
		Anschrift anschrift = Anschrift.createFromPayload(apiModel, new PostleitzahlLand(plz, Optional.of(schuleAPIModel)));

		// Assert
		assertEquals(hausnummer, anschrift.hausnummer());
		assertEquals(strasse, anschrift.strasse());
		assertEquals(ort, anschrift.ort());
		assertEquals(plz, anschrift.plz());
		assertEquals(schulname, anschrift.schulname());
		assertEquals("DE", anschrift.laendercode());

	}

}
