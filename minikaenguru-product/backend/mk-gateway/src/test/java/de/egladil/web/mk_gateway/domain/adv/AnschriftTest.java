// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VertragAdvAPIModel;

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

		VertragAdvAPIModel apiModel = new VertragAdvAPIModel().withHausnummer(hausnummer).withOrt(ort).withPlz(plz)
			.withSchulkuerzel(schulkuerzel).withSchulname(schulname).withStrasse(strasse);

		SchuleAPIModel schuleAPIModel = new SchuleAPIModel().withKuerzel(schulkuerzel).withKuerzelLand("DE-HE").withLand("Hessen")
			.withOrt(ort).withName("Schule 98765");

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
