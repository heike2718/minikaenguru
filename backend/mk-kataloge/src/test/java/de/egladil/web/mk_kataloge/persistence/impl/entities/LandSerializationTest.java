// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * LandSerializationTest
 */
public class LandSerializationTest {

	@Test
	void should_WriteAsJSON_when_allAttributesArePresent() throws JsonProcessingException {

		// Arrange
		Land land = new Land();
		land.setKuerzel("DE-HE");
		land.setName("Hessen");
		land.setAnzahlOrte(653);

		// Act
		String serialisaion = new ObjectMapper().writeValueAsString(land);

		// Assert
		// {"kuerzel":"DE-HE","name":"Hessen","anzahlOrte":653}

		// System.out.println(serialisaion);
		assertEquals(
			"{\"kuerzel\":\"DE-HE\",\"name\":\"Hessen\",\"anzahlOrte\":653}",
			serialisaion);

	}
}
