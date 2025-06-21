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
 * SchulePayloadTest
 */
@QuarkusTest
public class SchulePayloadTest {

	@Test
	void serialize() throws Exception {

		// Arrange
		SchulePayload schulePayload = SchulePayload.create("T5YY87JJ", "Test-Kairo-1", "3JVBB4IO", "Kairo", "EG", "Ägypten");
		schulePayload = schulePayload.withEmailAuftraggeber("vader@egladil.de");

		// Act
		String json = new ObjectMapper().writeValueAsString(schulePayload);

		// Assert

		System.out.println(json);
		assertEquals(
			"{\"kuerzel\":\"T5YY87JJ\",\"name\":\"Test-Kairo-1\",\"kuerzelOrt\":\"3JVBB4IO\",\"nameOrt\":\"Kairo\",\"kuerzelLand\":\"EG\",\"nameLand\":\"Ägypten\",\"emailAuftraggeber\":\"vader@egladil.de\"}",
			json);

	}

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
