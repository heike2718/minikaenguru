// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * SchuleSerializationTest
 */
public class SchuleSerializationTest {

	@Test
	void should_WriteAsJSON_when_allAttributesArePresent() throws JsonProcessingException {

		// Arrange
		Schule schule = new Schule();
		schule.setImportiertesKuerzel("S1V8W7SA");
		schule.setName("Krautgartenschule");
		schule.setLandKuerzel("DE-HE");
		schule.setLandName("Hessen");
		schule.setOrtKuerzel("E5AKHA1V");
		schule.setOrtName("Mainz-Kostheim");

		// Act
		String serialisaion = new ObjectMapper().writeValueAsString(schule);

		// Assert
		// {"kuerzel":null,"name":"Krautgartenschule","ortKuerzel":"E5AKHA1V","ortName":"Mainz-Kostheim","landKuerzel":"DE-HE","landName":"Hessen","importiertesKuerzel":"S1V8W7SA"}

		System.out.println(serialisaion);
		assertEquals(
			"{\"kuerzel\":null,\"name\":\"Krautgartenschule\",\"ortKuerzel\":\"E5AKHA1V\",\"ortName\":\"Mainz-Kostheim\",\"landKuerzel\":\"DE-HE\",\"landName\":\"Hessen\",\"importiertesKuerzel\":\"S1V8W7SA\"}",
			serialisaion);
	}

}
