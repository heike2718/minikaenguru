// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.apimodel.kataloge.SchulePayload;

/**
 * SchulePayloadTest
 */
public class SchulePayloadTest {

	@Test
	void should_Deserialize() throws Exception {

		// Arrange
		String json = "{\"kuerzel\":\"T5YY87JJ\",\"name\":\"Test-Kairo-1\",\"kuerzelOrt\":\"3JVBB4IO\",\"nameOrt\":\"Kairo\",\"kuerzelLand\":\"EG\",\"nameLand\":\"Ägypten\",\"emailAuftraggeber\":\"vader@egladil.de\"}";

		// Act
		SchulePayload schulePayload = new ObjectMapper().readValue(json.getBytes(), SchulePayload.class);

		// Assert
		assertEquals(
			"SchulePayload [kuerzel=T5YY87JJ, name=Test-Kairo-1, kuerzelOrt=3JVBB4IO, nameOrt=Kairo, kuerzelLand=EG, nameLand=Ägypten]",
			schulePayload.toString());
		assertEquals("T5YY87JJ", schulePayload.kuerzel());
	}

}
