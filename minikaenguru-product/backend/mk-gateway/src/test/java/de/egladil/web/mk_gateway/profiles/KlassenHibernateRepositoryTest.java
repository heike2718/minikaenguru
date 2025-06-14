// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.profiles;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.dao.KlassenHibernateRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;

/**
 * KlassenHibernateRepositoryTest
 */
@QuarkusTest
@TestProfile(FullDatabaseTestProfile.class)
public class KlassenHibernateRepositoryTest {

	@Inject
	KlassenHibernateRepository repository;

	@Test
	void should_findKlassenWithSchule_work() {

		// Arrange
		String schulkuerzel = "Z1GXJGBM";

		// Act
		List<Klasse> klassen = repository.findKlassenWithSchule(new Identifier(schulkuerzel));

		// Assert
		assertFalse(klassen.isEmpty());
	}

}
