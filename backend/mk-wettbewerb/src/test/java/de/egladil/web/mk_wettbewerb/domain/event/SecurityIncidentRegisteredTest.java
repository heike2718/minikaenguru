// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * SecurityIncidentRegisteredTest
 */
public class SecurityIncidentRegisteredTest {

	@Test
	void should_ConstructorSetAllAttributes_and_SerializationBeOk() throws JsonProcessingException {

		// Arrange
		String message = "Uiuiui - da ist ein schlimmer Finger im System.";

		// Act
		SecurityIncidentRegistered event = new SecurityIncidentRegistered(message);

		String serialization = new ObjectMapper().writeValueAsString(event);

		// System.out.println(serialization);

		// Assert
		assertEquals(
			"{\"message\":\"Uiuiui - da ist ein schlimmer Finger im System.\"}",
			serialization);
		assertNotNull(event.occuredOn());
		assertEquals("SecurityIncidentRegistered", event.typeName());
		assertEquals(message, event.message());
	}
}
