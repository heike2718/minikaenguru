// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_wettbewerb.domain.Identifier;

/**
 * PrivatpersonTest
 */
public class PrivatpersonTest {

	@Test
	void should_ConstructorThrowException_when_PersonNull() {

		try {

			new Privatperson(null, false, Arrays.asList(new Identifier("jsal")));
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("person darf nicht null sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_TeilnahmekuerzelNull() {

		try {

			new Privatperson(new Person("bhqgwhdq", "khdeih"), false, null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("teilnahmenummern darf nicht null sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorWithoutTeilnahmekuerzelInitAttributes() {

		// Arrange
		String uuid = "asuidgquoö";
		String fullName = "Grtq Jiesrtzq";
		Person person = new Person(uuid, fullName);

		// Act
		Privatperson privatperson = new Privatperson(person, false, Arrays.asList(new Identifier(uuid)));

		// Assert
		assertEquals(Rolle.PRIVAT, privatperson.rolle());
		assertEquals(uuid, privatperson.uuid());
		assertEquals(fullName, privatperson.fullName());
		assertEquals(person, privatperson.person());
		assertEquals(uuid, privatperson.persistierbareTeilnahmenummern());
		assertEquals(1, privatperson.teilnahmeIdentifier().size());
		assertFalse(privatperson.isNewsletterEmpfaenger());

	}

	@Test
	void should_ConstructorWithTeilnahmekuerzelInitAttributes() {

		// Arrange
		String uuid = "asuidgquoö";
		String fullName = "Grtq Jiesrtzq";
		Person person = new Person(uuid, fullName);
		List<Identifier> teilnahmekuerzel = new ArrayList<>();

		String id1 = "bla";
		String id2 = "blubb";

		teilnahmekuerzel.add(new Identifier(id1));
		teilnahmekuerzel.add(new Identifier(id2));

		// Act
		Privatperson privatperson = new Privatperson(person, true, teilnahmekuerzel);

		// Assert
		assertEquals(Rolle.PRIVAT, privatperson.rolle());
		assertEquals(uuid, privatperson.uuid());
		assertEquals(fullName, privatperson.fullName());
		assertEquals(person, privatperson.person());
		assertEquals("bla,blubb", privatperson.persistierbareTeilnahmenummern());
		assertEquals(2, privatperson.teilnahmeIdentifier().size());
		assertEquals("asuidgquoö - Grtq Jiesrtzq (PRIVAT)", privatperson.toString());
		assertTrue(privatperson.isNewsletterEmpfaenger());

	}

	@Test
	void should_EqualUsePersonAndRolle() {

		// Arrange
		List<Identifier> teilnahmekuerzel = new ArrayList<>();

		String id1 = "bla";
		String id2 = "blubb";

		teilnahmekuerzel.add(new Identifier(id1));
		teilnahmekuerzel.add(new Identifier(id2));

		Privatperson person1 = new Privatperson(new Person("u1", "n 1"), false, teilnahmekuerzel);
		Privatperson person2 = new Privatperson(new Person("u1", "n 1"), true, Arrays.asList(new Identifier("u1")));
		Privatperson person3 = new Privatperson(new Person("u2", "n 1"), false, teilnahmekuerzel);

		Veranstalter lehrer = new Lehrer(new Person("u1", "j s"), true, Arrays.asList(new Identifier("GHGFFIF")));

		// Assert
		assertEquals(person1, person1);
		assertEquals(person1, person2);
		assertEquals(person1.hashCode(), person2.hashCode());

		assertFalse(person1.equals(null));
		assertFalse(person1.equals(new Object()));
		assertFalse(person1.equals(person3));
		assertFalse(person1.hashCode() == person3.hashCode());
		assertFalse(person1.equals(lehrer));

	}

}
