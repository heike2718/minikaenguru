// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;

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

	@Test
	void should_DeserializeKollegenThrowException_when_JsonInvalid() {

		// Arrange
		String serialization = "[{\"uuid\":\"gsdgqu\",\"hallo\":\"Herr Bert\"},{\"uuid\":\"bakvsk\",\"fullName\":\"Frau Mann\"}]";

		SchulkollegienHibernateRepository repository = new SchulkollegienHibernateRepository();

		// Act
		try {

			repository.deserializeKollegen(serialization);
		} catch (MkGatewayRuntimeException e) {

			System.out.println(e.getMessage());

			assertEquals(
				"Konnte personen nicht deserialisieren: Unrecognized field \"hallo\" (class de.egladil.web.mk_gateway.domain.veranstalter.Person), not marked as ignorable (2 known properties: \"fullName\", \"uuid\"])\n"
					+
					" at [Source: (String)\"[{\"uuid\":\"gsdgqu\",\"hallo\":\"Herr Bert\"},{\"uuid\":\"bakvsk\",\"fullName\":\"Frau Mann\"}]\"; line: 1, column: 28] (through reference chain: java.lang.Object[][0]->de.egladil.web.mk_gateway.domain.veranstalter.Person[\"hallo\"])",
				e.getMessage());
		}

	}

}
