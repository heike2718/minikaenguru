// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
	void should_ConstructorThrowException_when_SchulkuerzelNull() {

		try {

			Person person = new Person("gqudqgi", "Hans Wurst");
			new LehrerChanged(person, "", null, false);

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("neueSchulkuerzel darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_SchulkuerzelBlank() {

		try {

			Person person = new Person("gqudqgi", "Hans Wurst");
			new LehrerChanged(person, "", "   ", false);

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("neueSchulkuerzel darf nicht blank sein.", e.getMessage());
		}
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
			"{\"person\":{\"uuid\":\"gqudqgi\",\"fullName\":\"Hans Wurst\"},\"alteSchulkuerzel\":\"\",\"neueSchulkuerzel\":\"jasqqh\",\"newsletterAbonnieren\":false}",
			body);
		assertEquals("LehrerChanged", eventObject.typeName());
		assertNotNull(eventObject.occuredOn());
	}

}
