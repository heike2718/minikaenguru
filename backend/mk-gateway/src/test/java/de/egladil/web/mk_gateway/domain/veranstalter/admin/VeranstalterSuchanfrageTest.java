// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.admin;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;

/**
 * VeranstalterSuchanfrageTest
 */
public class VeranstalterSuchanfrageTest {

	@Test
	void should_contructorInitAttributes() throws Exception {

		// Arrange
		String suchstring = "hallo";
		VeranstalterSuchkriterium suchkriterium = VeranstalterSuchkriterium.EMAIL;

		// Act
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(suchkriterium, suchstring);

		// Assert
		assertEquals("hallo", suchanfrage.getSuchstring());
		assertEquals(VeranstalterSuchkriterium.EMAIL, suchanfrage.getSuchkriterium());

		new ObjectMapper().writeValue(System.out, suchanfrage);

	}

}
