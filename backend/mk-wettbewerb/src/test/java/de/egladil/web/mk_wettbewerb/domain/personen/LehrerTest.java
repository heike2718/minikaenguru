// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.personen.Lehrer;
import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.personen.Rolle;

/**
 * LehrerTest
 */
public class LehrerTest {

	@Test
	void should_ConstructorInitRole() {

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
		Lehrer lehrer = new Lehrer(person, teilnahmekuerzel);

		// Assert
		assertEquals(Rolle.LEHRER, lehrer.rolle());
		assertEquals(uuid, lehrer.uuid());
		assertEquals(fullName, lehrer.fullName());
		assertEquals(person, lehrer.person());
		assertEquals("bla,blubb", lehrer.persistierbareTeilnahmekuerzel());
		assertEquals(2, lehrer.teilnahmekuerzel().size());
		assertEquals(lehrer.teilnahmekuerzel(), lehrer.schulen());

		assertEquals("Grtq Jiesrtzq (LEHRER)", lehrer.toString());
	}

}
