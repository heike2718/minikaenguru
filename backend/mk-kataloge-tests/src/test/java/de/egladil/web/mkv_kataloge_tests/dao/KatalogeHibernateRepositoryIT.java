// =====================================================
// Project: mk-kataloge-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_kataloge_tests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;
import de.egladil.web.mk_kataloge.infrastructure.persistence.impl.KatalogeHibernateRepository;
import de.egladil.web.mkv_kataloge_tests.AbstractIntegrationTest;

/**
 * KatalogeHibernateRepositoryTest
 */
public class KatalogeHibernateRepositoryIT extends AbstractIntegrationTest {

	private KatalogeHibernateRepository repository;

	@BeforeEach
	void setUp() {

		super.init();
		repository = KatalogeHibernateRepository.createForIntegrationTests(entityManager);
	}

	@Test
	void should_findSchulenInOrtWork() {

		// Arrange
		String kuerzelOrt = "27CM5KFF";
		String searchTerm = "Grundschule";

		// Act
		List<Schule> schulen = repository.findSchulenInOrt(kuerzelOrt, searchTerm);

		// Assert
		assertEquals(10, schulen.size());
	}

}
