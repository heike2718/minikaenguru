// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.veranstalter.Person;

/**
 * PersonTest
 */
public class PersonTest {

	@Test
	void should_ConstructorThrowException_when_UuidNull() {

		try {

			new Person(null, "agzidgwi");
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("uuid darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_UuidBlank() {

		try {

			new Person("   ", "agzidgwi");
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("uuid darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_FullNameNull() {

		try {

			new Person("agzidgwi", null);
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("fullName darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_FullNameBlank() {

		try {

			new Person("agzidgwi", "    ");
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("fullName darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorInitAttributes() {

		// Arrange
		String expectedUuid = "ajkgdasdi";
		String expectedFullName = "Ghfwhiow Tsvdj";

		// Act
		Person person = new Person(expectedUuid, expectedFullName);

		// Assert
		assertEquals(expectedUuid, person.uuid());
		assertEquals(expectedFullName, person.fullName());
		assertEquals("Person [uuid=ajkgdasdi, fullName=Ghfwhiow Tsvdj]", person.toString());
	}

	@Test
	void should_UuidDefineEqualPersons() {

		// Arrange
		Person person1 = new Person("qidfizqf", "Horst");
		Person person2 = new Person("qidfizqf", "Jürgen");
		Person person3 = new Person("asdiqi", "Horst");

		// Assert
		assertEquals(person1, person1);
		assertEquals(person1, person2);
		assertEquals(person1.hashCode(), person2.hashCode());

		assertFalse(person1.equals(null));
		assertFalse(person1.equals(new Object()));
		assertFalse(person1.equals(person3));
		assertFalse(person1.hashCode() == person3.hashCode());
	}

}
