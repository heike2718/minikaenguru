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
 * LehrerRegisteredForSchuleTest
 */
public class LehrerRegisteredForSchuleTest {

	@Test
	void should_ConstructorThrowException_when_PersonNull() {

		try {

			new LehrerRegisteredForSchule(null, "gadsguqi");

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("person darf nicht null sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_SchulkuerzelNull() {

		try {

			Person person = new Person("gqudqgi", "Hans Wurst");
			new LehrerRegisteredForSchule(person, null);

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulkuerzel darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_SchulkuerzelBlank() {

		try {

			Person person = new Person("gqudqgi", "Hans Wurst");
			new LehrerRegisteredForSchule(person, "   ");

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulkuerzel darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_Serialize_work() throws Exception {

		// Arrange
		String schulkuerzel = "jasqqh";
		Person person = new Person("gqudqgi", "Hans Wurst");

		LehrerRegisteredForSchule eventObject = new LehrerRegisteredForSchule(person, schulkuerzel);

		// Act
		String body = new ObjectMapper().writeValueAsString(eventObject);

		// Assert
		assertEquals("{\"person\":{\"uuid\":\"gqudqgi\",\"fullName\":\"Hans Wurst\"},\"schulkuerzel\":\"jasqqh\"}", body);
		assertEquals("LehrerRegisteredForSchule", eventObject.typeName());
		assertNotNull(eventObject.occuredOn());
	}

}
