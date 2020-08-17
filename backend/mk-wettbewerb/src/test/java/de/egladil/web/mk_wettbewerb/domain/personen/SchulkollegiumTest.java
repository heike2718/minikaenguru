// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_wettbewerb.domain.Identifier;

/**
 * SchulkollegiumTest
 */
public class SchulkollegiumTest {

	@Test
	void should_ConstructorThrowException_when_SchulkuerzelNull() {

		// Arrange
		Identifier schulkuerzel = null;
		Person[] personen = new Person[1];
		personen[0] = new Person("gsdgqu", "Herr Bert");

		// Act
		try {

			new Schulkollegium(schulkuerzel, personen);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulkuerzel darf nicht null sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_PersonenNull() {

		// Arrange
		Identifier schulkuerzel = new Identifier("JZHUF64T");

		// Act
		try {

			new Schulkollegium(schulkuerzel, null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("personen darf nicht null sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorInitAttributes() {

		// Arrange
		Identifier schulkuerzel = new Identifier("JZHUF64T");
		Person[] personen = new Person[2];
		personen[0] = new Person("gsdgqu", "Herr Bert");
		personen[1] = new Person("bakvsk", "Frau Mann");

		// Act
		Schulkollegium result = new Schulkollegium(schulkuerzel, personen);

		// Assert
		assertEquals("JZHUF64T", result.schulkuerzel().identifier());
		assertEquals(2, result.alleLehrerUnmodifiable().size());

		assertEquals("[{\"uuid\":\"gsdgqu\",\"fullName\":\"Herr Bert\"},{\"uuid\":\"bakvsk\",\"fullName\":\"Frau Mann\"}]",
			result.personenAlsJSON());
	}

	@Test
	void should_EqualsHashCode_be_CorrectlyImplemented() {

		// Arrange
		Schulkollegium result1 = null;
		Schulkollegium result2 = null;
		Schulkollegium result3 = null;

		{

			Identifier schulkuerzel = new Identifier("JZHUF64T");
			Person[] personen = new Person[2];
			personen[0] = new Person("gsdgqu", "Herr Bert");
			personen[1] = new Person("bakvsk", "Frau Mann");
			result1 = new Schulkollegium(schulkuerzel, personen);
		}

		{

			Identifier schulkuerzel = new Identifier("JZHUF64T");
			Person[] personen = new Person[1];
			personen[0] = new Person("hisdhpfhw", "Klaus Nikolaus");
			result2 = new Schulkollegium(schulkuerzel, personen);
		}

		{

			Identifier schulkuerzel = new Identifier("OU874FR3");
			Person[] personen = new Person[1];
			personen[0] = new Person("hisdhpfhw", "Klaus Nikolaus");
			result3 = new Schulkollegium(schulkuerzel, personen);
		}

		// Assert
		assertEquals(result1, result1);
		assertEquals(result1, result2);
		assertEquals(result1.hashCode(), result2.hashCode());
		assertFalse(result1.equals(null));

		assertFalse(result2.equals(result3));
		assertFalse(result1.equals(new Object()));

	}

}
