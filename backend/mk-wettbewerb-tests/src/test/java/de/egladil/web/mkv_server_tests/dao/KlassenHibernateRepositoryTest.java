// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KlassenHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * KlassenHibernateRepositoryTest
 */
public class KlassenHibernateRepositoryTest extends AbstractIT {

	private KlassenHibernateRepository repository;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		repository = KlassenHibernateRepository.createForIntegrationTest(entityManager);
	}

	@Test
	void should_findKlassenWithSchule_work() {

		// Arrange
		String schulkuerzel = "Z1GXJGBM";

		// Act
		List<Klasse> klassen = repository.findKlassenWithSchule(new Identifier(schulkuerzel));

		// Assert
		assertEquals(0, klassen.size());
	}

}
