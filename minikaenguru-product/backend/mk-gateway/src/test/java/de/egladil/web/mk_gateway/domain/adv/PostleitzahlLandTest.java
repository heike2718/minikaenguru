// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;

/**
 * PostleitzahlLandTest
 */
public class PostleitzahlLandTest {

	@Test
	void should_Init_when_keinSchulkatalogItem_and_PlzNumerisch() {

		// Arrange
		String plz = "76897 ";
		Optional<SchuleAPIModel> optSchule = Optional.empty();

		// Act
		PostleitzahlLand plzLand = new PostleitzahlLand(plz, optSchule);

		// Assert
		assertEquals("76897", plzLand.postleitzahl());
		assertEquals("DE", plzLand.landkuerzel());

	}

	@Test
	void should_Init_when_keinSchulkatalogItem_and_PlzMitLand() {

		// Arrange
		String plz = "CH - 8500 ";
		Optional<SchuleAPIModel> optSchule = Optional.empty();

		// Act
		PostleitzahlLand plzLand = new PostleitzahlLand(plz, optSchule);

		// Assert
		assertEquals("8500", plzLand.postleitzahl());
		assertEquals("CH", plzLand.landkuerzel());

	}

	@Test
	void should_Init_when_schulkatalogItemInDE_and_PlzNumerisch() {

		// Arrange
		String plz = "76897 ";
		SchuleAPIModel schuleAPModel = SchuleAPIModel.withKuerzelLand("DE-TH");
		Optional<SchuleAPIModel> optSchule = Optional.of(schuleAPModel);

		// Act
		PostleitzahlLand plzLand = new PostleitzahlLand(plz, optSchule);

		// Assert
		assertEquals("76897", plzLand.postleitzahl());
		assertEquals("DE", plzLand.landkuerzel());

	}

	@Test
	void should_Init_when_schulkatalogItemInCH_and_PlzNumerisch() {

		// Arrange
		String plz = "76897 ";
		SchuleAPIModel schuleAPModel = SchuleAPIModel.withKuerzelLand("CH");
		Optional<SchuleAPIModel> optSchule = Optional.of(schuleAPModel);

		// Act
		PostleitzahlLand plzLand = new PostleitzahlLand(plz, optSchule);

		// Assert
		assertEquals("76897", plzLand.postleitzahl());
		assertEquals("CH", plzLand.landkuerzel());

	}

	@Test
	void should_Init_when_schulkatalogItemInCH_and_PlzNichtNumerischMitAnderemKuerzel() {

		// Arrange
		String plz = "TH-76897 ";
		SchuleAPIModel schuleAPModel = SchuleAPIModel.withKuerzelLand("CH");
		Optional<SchuleAPIModel> optSchule = Optional.of(schuleAPModel);

		// Act
		PostleitzahlLand plzLand = new PostleitzahlLand(plz, optSchule);

		// Assert
		assertEquals("76897", plzLand.postleitzahl());
		assertEquals("CH", plzLand.landkuerzel());

	}

	@Test
	void should_Init_when_schulkatalogItemInCH_and_PlzNichtNumerischMitGleichemKuerzel() {

		// Arrange
		String plz = " CH -76897 ";
		SchuleAPIModel schuleAPModel = SchuleAPIModel.withKuerzelLand("CH");
		Optional<SchuleAPIModel> optSchule = Optional.of(schuleAPModel);

		// Act
		PostleitzahlLand plzLand = new PostleitzahlLand(plz, optSchule);

		// Assert
		assertEquals("76897", plzLand.postleitzahl());
		assertEquals("CH", plzLand.landkuerzel());

	}

}
