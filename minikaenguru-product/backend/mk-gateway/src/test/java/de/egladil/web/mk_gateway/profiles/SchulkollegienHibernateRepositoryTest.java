// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.profiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.veranstalter.Kollege;
import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.dao.SchulkollegienHibernateRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;

/**
 * SchulkollegienHibernateRepositoryTest
 */
@QuarkusTest
@TestProfile(FullDatabaseTestProfile.class)
public class SchulkollegienHibernateRepositoryTest {

	@Inject
	SchulkollegienHibernateRepository repository;

	@Test
	void should_DeserializeKollegen_work() {

		// Arrange
		String serialization = "[{\"uuid\":\"gsdgqu\",\"fullName\":\"Herr Bert\"},{\"uuid\":\"bakvsk\",\"fullName\":\"Frau Mann\"}]";

		// Act
		Kollege[] personen = repository.deserializeKollegen(serialization);

		// Assert
		assertEquals(2, personen.length);

		{

			Kollege person = personen[0];
			assertEquals("gsdgqu", person.uuid());
			assertEquals("Herr Bert", person.fullName());
		}

		{

			Kollege person = personen[1];
			assertEquals("bakvsk", person.uuid());
			assertEquals("Frau Mann", person.fullName());
		}
	}

	@Test
	void should_DeserializeKollegenThrowException_when_JsonInvalid() {

		// Arrange
		String serialization = "[{\"uuid\":\"gsdgqu\",\"hallo\":\"Herr Bert\"},{\"uuid\":\"bakvsk\",\"fullName\":\"Frau Mann\"}]";

		// Act
		try {

			repository.deserializeKollegen(serialization);
		} catch (MkGatewayRuntimeException e) {

			System.out.println(e.getMessage());

			assertTrue(e.getMessage().startsWith("Konnte personen nicht deserialisieren: Unrecognized field \"hallo\""));
		}

	}

}
