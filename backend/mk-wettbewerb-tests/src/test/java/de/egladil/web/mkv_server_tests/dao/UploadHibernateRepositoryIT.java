// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
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
		List<UploadsMonitoringViewItem> uploads = repository.loadUploadsPage(5, 0);

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

	@Test
	void should_countAuspraegungenForTeilnahmeByColumnNameWork() {

		// Arrange
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmenummer("M5ZD2NL2");

		// Act
		List<Auspraegung> result = repository.countAuspraegungenForTeilnahmeByColumnName(teilnahmeIdentifier, "UPLOAD_TYPE");

		// Assert
		Optional<Auspraegung> optAuspraegung = result.stream().filter(a -> UploadType.AUSWERTUNG.toString().equals(a.getWert()))
			.findFirst();
		assertTrue(optAuspraegung.isEmpty());

		optAuspraegung = result.stream().filter(a -> UploadType.KLASSENLISTE.toString().equals(a.getWert()))
			.findFirst();
		assertEquals(Long.valueOf(2), Long.valueOf(optAuspraegung.get().getAnzahl()));
	}

}
