// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * LehrerChangedTest
 */
public class LehrerChangedTest {

	@Test
	void should_ConstructorThrowException_when_PersonNull() {

		try {

			new LehrerChanged(null, "", "gadsguqi", false);

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("person darf nicht null sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorAcceptBlankSchulkuerzel_when_SchulkuerzelNull() {

		Person person = new Person("gqudqgi", "Hans Wurst");
		LehrerChanged eventPayload = new LehrerChanged(person, "", null, false);
		assertNull(eventPayload.alteSchulkuerzel());
		assertNull(eventPayload.neueSchulkuerzel());
	}

	@Test
	void should_ConstructorAcceptBlankSchulkuerzel_when_SchulkuerzelBlank() {

		Person person = new Person("gqudqgi", "Hans Wurst");
		LehrerChanged eventPayload = new LehrerChanged(person, "", "   ", false);
		assertNull(eventPayload.alteSchulkuerzel());
		assertNull(eventPayload.neueSchulkuerzel());
	}

	@Test
	void should_Serialize_work() throws Exception {

		// Arrange
		String schulkuerzel = "jasqqh";
		Person person = new Person("gqudqgi", "Hans Wurst");

		LehrerChanged eventObject = new LehrerChanged(person, "", schulkuerzel, false);

		// Act
		String body = new ObjectMapper().writeValueAsString(eventObject);

		// Assert
		assertEquals(
			"{\"person\":{\"uuid\":\"gqudqgi\",\"fullName\":\"Hans Wurst\",\"email\":null},\"alteSchulkuerzel\":null,\"neueSchulkuerzel\":\"jasqqh\",\"newsletterAbonnieren\":false}",
			body);
		assertEquals("LehrerChanged", eventObject.typeName());
		assertNotNull(eventObject.occuredOn());
	}

}
