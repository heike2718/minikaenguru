// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * AuswertungimportZeileTest
 */
public class AuswertungimportZeileTest {

	@Test
	void should_getWertungscodeWork_when_rohdatenFromOdsOk() {

		// Arrange
		String rohdaten = "Malte,r,,f,,r,,r,,r,,r,,r,,r,,r,,r,,f,,r,,";
		String expected = "rfrrrrrrrrfr";

		AuswertungimportZeile zeile = new AuswertungimportZeile().withRohdaten(rohdaten);

		// Act
		String result = zeile.getWertungscode();

		// Assert
		assertEquals(expected, result);
	}

	@Test
	void should_getWertungscodeWork_when_rohdatenFromExcelOk() {

		// Arrange
		String rohdaten = "Serwan,r,3,r,3.0,f,-0.75,n,0.0,n,0.0,n,0.0,n,0.0,n,0.0,n,0.0,n,0.0,n,0.0,n,0.0,n,0.0,n,0.0,n,0.0,20.25";
		String expected = "rrfnnnnnnnnnnnn";

		AuswertungimportZeile zeile = new AuswertungimportZeile().withRohdaten(rohdaten);

		// Act
		String result = zeile.getWertungscode();

		// Assert
		assertEquals(expected, result);
	}

}
