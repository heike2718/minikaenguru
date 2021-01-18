// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * SchulkollegiumTest
 */
public class SchulkollegiumTest {

	@Test
	void should_ConstructorThrowException_when_SchulkuerzelNull() {

		// Arrange
		Identifier schulkuerzel = null;
		Kollege[] personen = new Kollege[1];
		personen[0] = new Kollege("gsdgqu", "Herr Bert");

		// Act
		try {

			new Schulkollegium(schulkuerzel, personen);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulkuerzel darf nicht null sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_KollegenNull() {

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
		Kollege[] personen = new Kollege[2];
		personen[0] = new Kollege("gsdgqu", "Herr Bert");
		personen[1] = new Kollege("bakvsk", "Frau Mann");

		// Act
		Schulkollegium result = new Schulkollegium(schulkuerzel, personen);

		// Assert
		assertEquals("JZHUF64T", result.schulkuerzel().identifier());
		assertEquals(2, result.alleLehrerUnmodifiable().size());

		assertEquals(
			"[{\"uuid\":\"gsdgqu\",\"fullName\":\"Herr Bert\"},{\"uuid\":\"bakvsk\",\"fullName\":\"Frau Mann\"}]",
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
			Kollege[] personen = new Kollege[2];
			personen[0] = new Kollege("gsdgqu", "Herr Bert");
			personen[1] = new Kollege("bakvsk", "Frau Mann");
			result1 = new Schulkollegium(schulkuerzel, personen);
		}

		{

			Identifier schulkuerzel = new Identifier("JZHUF64T");
			Kollege[] personen = new Kollege[1];
			personen[0] = new Kollege("hisdhpfhw", "Klaus Nikolaus");
			result2 = new Schulkollegium(schulkuerzel, personen);
		}

		{

			Identifier schulkuerzel = new Identifier("OU874FR3");
			Kollege[] personen = new Kollege[1];
			personen[0] = new Kollege("hisdhpfhw", "Klaus Nikolaus");
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

	@Test
	void should_serializeAsJson_work() throws Exception {

		// Arrange

		List<Schulkollegium> kollegien = new ArrayList<>();

		{

			Identifier schulkuerzel = new Identifier("SCHULKUERZEL_1");
			Kollege[] personen = new Kollege[4];
			personen[0] = new Kollege("UUID_LEHRER_1", "Hans Wurst");
			personen[1] = new Kollege("UUID_LEHRER_2", "Olle Keule");
			personen[2] = new Kollege("UUID_LEHRER_3", "Mimi Mimimi");
			personen[3] = new Kollege("UUID_LEHRER_GESPERRT", "Gesperrter Lehrer");
			Schulkollegium result = new Schulkollegium(schulkuerzel, personen);

			kollegien.add(result);
		}

		{

			Identifier schulkuerzel = new Identifier("SCHULKUERZEL_2");
			Kollege[] personen = new Kollege[1];
			personen[0] = new Kollege("UUID_LEHRER_1", "Hans Wurst");
			Schulkollegium result = new Schulkollegium(schulkuerzel, personen);

			kollegien.add(result);
		}

		{

			Identifier schulkuerzel = new Identifier("SCHULKUERZEL_3");
			Kollege[] personen = new Kollege[1];
			personen[0] = new Kollege("UUID_LEHRER_ANDERE_SCHULE", "Lehrer andere Schule");
			Schulkollegium result = new Schulkollegium(schulkuerzel, personen);

			kollegien.add(result);
		}

		// Act
		String json = new ObjectMapper().writeValueAsString(kollegien);

		// Assert
		System.out.println(json);
	}

}
