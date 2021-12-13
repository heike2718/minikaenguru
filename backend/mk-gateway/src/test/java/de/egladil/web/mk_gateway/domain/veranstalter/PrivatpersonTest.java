// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * PrivatpersonTest
 */
public class PrivatpersonTest {

	@Test
	void should_ConstructorThrowException_when_PersonNull() {

		try {

			new Privatveranstalter(null, false, Arrays.asList(new Identifier("jsal")));
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("person darf nicht null sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_TeilnahmekuerzelNull() {

		try {

			new Privatveranstalter(new Person("bhqgwhdq", "khdeih"), false, null);
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
		Privatveranstalter privatveranstalter = new Privatveranstalter(person, false, Arrays.asList(new Identifier(uuid)));

		// Assert
		assertEquals(Rolle.PRIVAT, privatveranstalter.rolle());
		assertEquals(uuid, privatveranstalter.uuid());
		assertEquals(fullName, privatveranstalter.fullName());
		assertEquals(person, privatveranstalter.person());
		assertEquals(uuid, privatveranstalter.persistierbareTeilnahmenummern());
		assertEquals(1, privatveranstalter.teilnahmeIdentifier().size());
		assertFalse(privatveranstalter.isNewsletterEmpfaenger());

	}

	@Test
	void should_ConstructorWithTeilnahmekuerzelInitAttributes() throws Exception {

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
		Privatveranstalter privatveranstalter = new Privatveranstalter(person, true, teilnahmekuerzel);

		// Assert
		assertEquals(Rolle.PRIVAT, privatveranstalter.rolle());
		assertEquals(uuid, privatveranstalter.uuid());
		assertEquals(fullName, privatveranstalter.fullName());
		assertEquals(person, privatveranstalter.person());
		assertEquals("bla,blubb", privatveranstalter.persistierbareTeilnahmenummern());
		assertEquals(2, privatveranstalter.teilnahmeIdentifier().size());
		assertEquals("asuidgquoö - Grtq Jiesrtzq (PRIVAT)", privatveranstalter.toString());
		assertTrue(privatveranstalter.isNewsletterEmpfaenger());

		new ObjectMapper().writeValue(System.out, privatveranstalter);

	}

	@Test
	void should_EqualUsePersonAndRolle() {

		// Arrange
		List<Identifier> teilnahmekuerzel = new ArrayList<>();

		String id1 = "bla";
		String id2 = "blubb";

		teilnahmekuerzel.add(new Identifier(id1));
		teilnahmekuerzel.add(new Identifier(id2));

		Privatveranstalter person1 = new Privatveranstalter(new Person("u1", "n 1"), false, teilnahmekuerzel);
		Privatveranstalter person2 = new Privatveranstalter(new Person("u1", "n 1"), true, Arrays.asList(new Identifier("u1")));
		Privatveranstalter person3 = new Privatveranstalter(new Person("u2", "n 1"), false, teilnahmekuerzel);

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

	@Test
	void should_Deserialize() throws Exception {

		try (InputStream in = getClass().getResourceAsStream("/privatperson.json")) {

			Privatveranstalter privatveranstalter = new ObjectMapper().readValue(in, Privatveranstalter.class);

			Person person = privatveranstalter.person();
			assertEquals("asuidgquoö", person.uuid());
			assertEquals("Grtq Jiesrtzq", person.fullName());

			assertTrue(privatveranstalter.isNewsletterEmpfaenger());
			assertEquals(ZugangUnterlagen.DEFAULT, privatveranstalter.zugangUnterlagen());

			List<Identifier> teilnahmenummern = privatveranstalter.teilnahmeIdentifier();
			assertEquals(2, teilnahmenummern.size());
			assertEquals("bla", teilnahmenummern.get(0).identifier());
			assertEquals("blubb", teilnahmenummern.get(1).identifier());

		}
	}

}
