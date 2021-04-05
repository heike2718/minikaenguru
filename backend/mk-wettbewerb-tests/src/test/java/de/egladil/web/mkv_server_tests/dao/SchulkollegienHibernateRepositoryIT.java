// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.veranstalter.Kollege;
import de.egladil.web.mk_gateway.domain.veranstalter.SchulkollegienRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.Schulkollegium;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.SchulkollegienHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * SchulkollegienHibernateRepositoryIT
 */
public class SchulkollegienHibernateRepositoryIT extends AbstractIntegrationTest {

	private SchulkollegienRepository schulkollegienRepository;

	@BeforeEach
	@Override
	protected void setUp() {

		super.setUp();
		schulkollegienRepository = SchulkollegienHibernateRepository.createForIntegrationTest(entityManager);
	}

	@Test
	void should_loadSchulkollegien() {

		// Arrange
		String schulkuerzel = "ZYOP42TB";

		// Act
		Optional<Schulkollegium> opt = schulkollegienRepository.ofSchulkuerzel(new Identifier(schulkuerzel));

		// Assert
		assertFalse(opt.isEmpty());

		Schulkollegium kollegium = opt.get();

		List<Kollege> alle = kollegium.alleLehrerUnmodifiable();
		assertEquals(2, alle.size());

	}

}
