// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DataInconsistencyRegisteredTest
 */
public class DataInconsistencyRegisteredTest {

	@Test
	void should_ConstructorSetAllAttributes_and_SerializationBeOk() throws JsonProcessingException {

		// Arrange
		String message = "Da ist etwas bei der Migration über die Wupper gehupft.";

		// Act
		DataInconsistencyRegistered event = new DataInconsistencyRegistered(message);

		String serialization = new ObjectMapper().writeValueAsString(event);

		// System.out.println(serialization);

		// Assert
		assertEquals(
			"{\"message\":\"Da ist etwas bei der Migration über die Wupper gehupft.\"}",
			serialization);
		assertNotNull(event.occuredOn());
		assertEquals("DataInconsistencyRegistered", event.typeName());
		assertEquals(message, event.message());
	}
}
