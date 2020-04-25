// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_wettbewerb.domain.model.personen.Person;

/**
 * SchulkollegienHibernateRepositoryTest
 */
public class SchulkollegienHibernateRepositoryTest {

	@Test
	void should_DeserializeKollegen_work() {

		// Arrange
		String serialization = "[{\"uuid\":\"gsdgqu\",\"fullName\":\"Herr Bert\"},{\"uuid\":\"bakvsk\",\"fullName\":\"Frau Mann\"}]";

		SchulkollegienHibernateRepository repository = new SchulkollegienHibernateRepository();

		// Act
		Person[] personen = repository.deserializeKollegen(serialization);

		// Assert
		assertEquals(2, personen.length);

		{

			Person person = personen[0];
			assertEquals("gsdgqu", person.uuid());
			assertEquals("Herr Bert", person.fullName());
		}

		{

			Person person = personen[1];
			assertEquals("bakvsk", person.uuid());
			assertEquals("Frau Mann", person.fullName());
		}
	}

}
