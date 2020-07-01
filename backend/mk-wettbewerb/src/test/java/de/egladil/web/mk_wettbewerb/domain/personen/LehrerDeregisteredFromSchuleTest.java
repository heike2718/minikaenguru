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
 * LehrerDeregisteredFromSchuleTest
 */
public class LehrerDeregisteredFromSchuleTest {

	@Test
	void should_ConstructorThrowException_when_PersonNull() {

		// Arrange
		try {

			new LehrerDeregistredFromSchule(null, "gadsguqi");

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("person darf nicht null sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_SchulkuerzelNull() {

		try {

			Person person = new Person("gqudqgi", "Hans Wurst");
			new LehrerDeregistredFromSchule(person, null);

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulkuerzel darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_SchulkuerzelBlank() {

		try {

			Person person = new Person("gqudqgi", "Hans Wurst");
			new LehrerDeregistredFromSchule(person, "   ");

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

		LehrerDeregistredFromSchule eventObject = new LehrerDeregistredFromSchule(person, schulkuerzel);

		// Act
		String body = new ObjectMapper().writeValueAsString(eventObject);

		// Assert
		assertEquals("{\"person\":{\"uuid\":\"gqudqgi\",\"fullName\":\"Hans Wurst\"},\"schulkuerzel\":\"jasqqh\"}", body);
		assertEquals("LehrerDeregistredFromSchule", eventObject.typeName());
		assertNotNull(eventObject.occuredOn());
	}
}
