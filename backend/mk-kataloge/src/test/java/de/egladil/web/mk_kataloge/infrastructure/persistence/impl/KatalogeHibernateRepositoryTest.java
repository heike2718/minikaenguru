// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * KatalogeHibernateRepositoryTest
 */
@QuarkusTest
public class KatalogeHibernateRepositoryTest {

	@Inject
	KatalogeHibernateRepository repository;

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
