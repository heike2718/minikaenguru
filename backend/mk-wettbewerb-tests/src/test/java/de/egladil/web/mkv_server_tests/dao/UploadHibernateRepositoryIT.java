// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.UploadsMonitoringViewItem;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UploadHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * UploadHibernateRepositoryIT
 */
public class UploadHibernateRepositoryIT extends AbstractIntegrationTest {

	UploadHibernateRepository repository;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		repository = UploadHibernateRepository.createForIntegrationTests(entityManager);
	}

	@Test
	void should_findUploadsWithTeilnahmenummer_work() {

		// Arrange
		String teilnahmenummer = "M5ZD2NL2";
		UploadType uploadType = UploadType.KLASSENLISTE;

		// Act
		List<UploadsMonitoringViewItem> uploads = repository.findUploadsWithUploadTypeAndTeilnahmenummer(uploadType,
			teilnahmenummer);

		// Assert
		assertEquals(2, uploads.size());

	}

	@Test
	void should_loadPage_work() {

		// Arrange
		// Act
		List<UploadsMonitoringViewItem> uploads = repository.loadUploadsPage(10, 0);

		// Assert
		assertEquals(2, uploads.size());

	}

	@Test
	void should_countUploadsWithTeilnahmenummer_work() {

		// Arrange
		String teilnahmenummer = "M5ZD2NL2";
		UploadType uploadType = UploadType.KLASSENLISTE;

		// Act
		long uploads = repository.countUploadsWithUploadTypeAndTeilnahmenummer(uploadType, teilnahmenummer);

		// Assert
		assertEquals(2, uploads);

	}

}
