// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.apimodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * StringsAPIModelTest
 */
public class StringsAPIModelTest {

	@Test
	void should_addOnlyNotNullAndNotTwice() {

		// Arrange
		StringsAPIModel model = new StringsAPIModel();

		// Act
		model.addString("ZZO6ZF1V");
		model.addString("ZZO6ZF1V");
		model.addString(null);
		model.addString("ETNCCRVN");

		// Assert
		assertEquals(2, model.getStrings().size());

	}

	@Test
	void should_serialize() throws JsonProcessingException {

		// Arrange
		StringsAPIModel model = new StringsAPIModel();
		model.addString("ZZO6ZF1V");
		model.addString("PHP0BZS7");

		// Act
		String serialization = new ObjectMapper().writeValueAsString(model);

		// {"strings":["ZZO6ZF1V","PHP0BZS7"]}
		assertEquals("{\"strings\":[\"ZZO6ZF1V\",\"PHP0BZS7\"]}", serialization);

	}

}
