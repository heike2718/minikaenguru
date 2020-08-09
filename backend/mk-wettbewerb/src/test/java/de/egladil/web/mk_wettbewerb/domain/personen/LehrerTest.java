// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_wettbewerb.domain.Identifier;

/**
 * LehrerTest
 */
public class LehrerTest {

	@Test
	void should_ConstructorInitRole() throws JsonGenerationException, JsonMappingException, IOException {

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
		Lehrer lehrer = new Lehrer(person, false, teilnahmekuerzel);

		// Assert
		assertEquals(Rolle.LEHRER, lehrer.rolle());
		assertEquals(uuid, lehrer.uuid());
		assertEquals(fullName, lehrer.fullName());
		assertEquals(person, lehrer.person());
		assertEquals("bla,blubb", lehrer.persistierbareTeilnahmenummern());
		assertEquals(2, lehrer.teilnahmeIdentifier().size());
		assertEquals(lehrer.teilnahmeIdentifier(), lehrer.schulen());
		assertFalse(lehrer.isNewsletterEmpfaenger());

		assertEquals("asuidgquoö - Grtq Jiesrtzq (LEHRER)", lehrer.toString());

		new ObjectMapper().writeValue(System.out, lehrer);
	}

	@Test
	void should_ConstructorInitFlagNewsletter() throws JsonGenerationException, JsonMappingException, IOException {

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
		Lehrer lehrer = new Lehrer(person, true, teilnahmekuerzel);

		// Assert
		assertEquals(Rolle.LEHRER, lehrer.rolle());
		assertEquals(uuid, lehrer.uuid());
		assertEquals(fullName, lehrer.fullName());
		assertEquals(person, lehrer.person());
		assertEquals("bla,blubb", lehrer.persistierbareTeilnahmenummern());
		assertEquals(2, lehrer.teilnahmeIdentifier().size());
		assertEquals(lehrer.teilnahmeIdentifier(), lehrer.schulen());
		assertTrue(lehrer.isNewsletterEmpfaenger());

		assertEquals("asuidgquoö - Grtq Jiesrtzq (LEHRER)", lehrer.toString());

		new ObjectMapper().writeValue(System.out, lehrer);
	}

}
