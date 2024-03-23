// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.apimodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

/**
 * SchulkatalogAntragTest
 */
@QuarkusTest
public class SchulkatalogAntragTest {

	@Test
	void serialize() throws Exception {

		// Arrange
		SchulkatalogAntrag antrag = SchulkatalogAntrag.createForTest("bla@web.de", "Hessen",
			"Mainz-Kastel", "55252", "Blümchenschule", "Mainzer Straße 15", "");

		// Act
		String json = new ObjectMapper().writeValueAsString(antrag);

		// Assert

		System.out.println(json);
		assertEquals(
			"{\"email\":\"bla@web.de\",\"land\":\"Hessen\",\"ort\":\"Mainz-Kastel\",\"plz\":\"55252\",\"schulname\":\"Blümchenschule\",\"strasseUndHausnummer\":\"Mainzer Straße 15\",\"kleber\":\"\"}",
			json);

	}

}
