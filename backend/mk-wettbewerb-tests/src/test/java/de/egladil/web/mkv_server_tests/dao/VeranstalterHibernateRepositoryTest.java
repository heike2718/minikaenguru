// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.admin.VeranstalterSuchkriterium;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VeranstalterHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * VeranstalterHibernateRepositoryTest
 */
public class VeranstalterHibernateRepositoryTest extends AbstractIT {

	VeranstalterHibernateRepository veranstalterRepository;

	@BeforeEach
	protected void setUp() {

		super.setUp();
		veranstalterRepository = VeranstalterHibernateRepository.createForIntegrationTest(entityManager);

	}

	@Test
	void should_findByEmail_work() {

		// Arrange
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(VeranstalterSuchkriterium.EMAIL, "tl");

		// Act
		List<Veranstalter> result = veranstalterRepository.findVeranstalter(suchanfrage);

		// Assert
		assertEquals(5, result.size());
	}

	@Test
	void should_findByName_work() {

		// Arrange
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(VeranstalterSuchkriterium.NAME, "beutlin");

		// Act
		List<Veranstalter> result = veranstalterRepository.findVeranstalter(suchanfrage);

		// Assert
		assertEquals(2, result.size());
	}

	@Test
	void should_findByUuid_work() {

		// Arrange
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(VeranstalterSuchkriterium.UUID, "5d89");

		// Act
		List<Veranstalter> result = veranstalterRepository.findVeranstalter(suchanfrage);

		// Assert
		assertEquals(1, result.size());
	}

	@Test
	void should_findByTeilnahmenummer_work() {

		// Arrange
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(VeranstalterSuchkriterium.TEILNAHMENUMMER, "EEGEECP6");

		// Act
		List<Veranstalter> result = veranstalterRepository.findVeranstalter(suchanfrage);

		// Assert
		assertEquals(3, result.size());
	}

}
