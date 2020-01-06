// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_commons.dao.AbstractIntegrationTest;
import de.egladil.web.mk_commons.dao.IAuswertungDownloadDao;
import de.egladil.web.mk_commons.domain.impl.AuswertungDownload;

/**
 * AuswertungDownloadDaoTest
 */
class AuswertungDownloadDaoTest extends AbstractIntegrationTest {

	private IAuswertungDownloadDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new AuswertungDownloadDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<AuswertungDownload> trefferliste = dao.load(AuswertungDownload.class);

		// Assert
		assertEquals(40, trefferliste.size());
	}

}
