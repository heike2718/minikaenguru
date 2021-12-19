// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.uploadmonitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.uploadmonitoring.api.UploadMonitoringInfo;
import de.egladil.web.mk_gateway.domain.uploadmonitoring.impl.UploadMonitoringServiceImpl;
import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * UploadKlassenlisteMonitoringServiceImplIT
 */
public class UploadKlassenlisteMonitoringServiceImplIT extends AbstractIntegrationTest {

	UploadMonitoringServiceImpl service;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		service = UploadMonitoringServiceImpl.createForIntegrationTests(entityManager);
	}

	@Test
	void should_findUploadsKlassenlisteWithTeilnahmenummer_work() {

		// Arrange
		String teilnahmenummer = "M5ZD2NL2";

		// Act
		List<UploadMonitoringInfo> uploadInfos = service.findUploadsKlassenlisteWithTeilnahmenummer(teilnahmenummer);

		// Assert
		assertEquals(2, uploadInfos.size());

		{

			UploadMonitoringInfo info = uploadInfos.get(0);
			assertEquals("vornach@egladil.de", info.getEmailLehrer());
			assertEquals("vor nach", info.getNameLehrer());
			assertEquals(UploadStatus.DATENFEHLER, info.getUploadStatus());
			assertEquals(DateiTyp.ODS, info.getDateiTyp());
			assertEquals(UploadType.KLASSENLISTE, info.getUploadType());
			assertEquals(teilnahmenummer, info.getTeilnahmenummer());
			assertEquals("Grundschule \"Im Rosental\"", info.getNameSchule());
			assertEquals("06.11.2021 18:59:34", info.getUploadDatum());
		}

		{

			UploadMonitoringInfo info = uploadInfos.get(1);
			assertEquals("vornach@egladil.de", info.getEmailLehrer());
			assertEquals("vor nach", info.getNameLehrer());
			assertEquals(UploadStatus.DATENFEHLER, info.getUploadStatus());
			assertEquals(DateiTyp.ODS, info.getDateiTyp());
			assertEquals(UploadType.KLASSENLISTE, info.getUploadType());
			assertEquals(teilnahmenummer, info.getTeilnahmenummer());
			assertEquals("Grundschule \"Im Rosental\"", info.getNameSchule());
			assertEquals("06.11.2021 20:04:05", info.getUploadDatum());
		}
	}

	@Test
	void should_loadUploads_work() {

		// Arrange
		String teilnahmenummer = "M5ZD2NL2";

		// Act
		List<UploadMonitoringInfo> uploadInfos = service.loadUploads(10, 0);

		// Assert
		assertEquals(2, uploadInfos.size());

		{

			UploadMonitoringInfo info = uploadInfos.get(0);
			assertEquals("vornach@egladil.de", info.getEmailLehrer());
			assertEquals("vor nach", info.getNameLehrer());
			assertEquals(UploadStatus.DATENFEHLER, info.getUploadStatus());
			assertEquals(DateiTyp.ODS, info.getDateiTyp());
			assertEquals(UploadType.KLASSENLISTE, info.getUploadType());
			assertEquals(teilnahmenummer, info.getTeilnahmenummer());
			assertEquals("Grundschule \"Im Rosental\"", info.getNameSchule());
			assertEquals("06.11.2021 18:59:34", info.getUploadDatum());
		}

		{

			UploadMonitoringInfo info = uploadInfos.get(1);
			assertEquals("vornach@egladil.de", info.getEmailLehrer());
			assertEquals("vor nach", info.getNameLehrer());
			assertEquals(UploadStatus.DATENFEHLER, info.getUploadStatus());
			assertEquals(DateiTyp.ODS, info.getDateiTyp());
			assertEquals(UploadType.KLASSENLISTE, info.getUploadType());
			assertEquals(teilnahmenummer, info.getTeilnahmenummer());
			assertEquals("Grundschule \"Im Rosental\"", info.getNameSchule());
			assertEquals("06.11.2021 20:04:05", info.getUploadDatum());
		}
	}

}
