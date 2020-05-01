// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.personen.LehrerRegisteredForSchule;

/**
 * LehrerRegisteredForSchuleTest
 */
public class LehrerRegisteredForSchuleTest {

	@Test
	void should_ConstructorThrowException_when_PersonNull() {

		try {

			new LehrerRegisteredForSchule("gadsguqi", null);

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("person darf nicht null sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_SchulkuerzelNull() {

		try {

			Person person = new Person("gqudqgi", "Hans Wurst");
			new LehrerRegisteredForSchule(null, person);

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulkuerzel darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_SchulkuerzelBlank() {

		try {

			Person person = new Person("gqudqgi", "Hans Wurst");
			new LehrerRegisteredForSchule("   ", person);

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulkuerzel darf nicht blank sein.", e.getMessage());
		}
	}

}
