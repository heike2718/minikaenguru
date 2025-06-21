// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;

/**
 * OrtSerializationTest
 */
public class OrtSerializationTest {

	@Test
	void should_WriteAsJSON_when_allAttributesArePresent() throws JsonProcessingException {

		// Arrange
		Ort ort = new Ort();
		ort.setAnzahlSchulen(3);
		ort.setKuerzel("E5AKHA1V");
		ort.setName("Mainz-Kostheim");
		ort.setLandKuerzel("DE-HE");
		ort.setLandName("Hessen");

		// Act
		String serialisaion = new ObjectMapper().writeValueAsString(ort);

		// Assert
		// {"kuerzel":"E5AKHA1V","name":"Mainz-Kostheim","anzahlSchulen":3,"landKuerzel":"DE-HE","landName":"Hessen"}

		// System.out.println(serialisaion);
		assertEquals(
			"{\"kuerzel\":\"E5AKHA1V\",\"name\":\"Mainz-Kostheim\",\"anzahlSchulen\":3,\"landKuerzel\":\"DE-HE\",\"landName\":\"Hessen\"}",
			serialisaion);

	}

}
