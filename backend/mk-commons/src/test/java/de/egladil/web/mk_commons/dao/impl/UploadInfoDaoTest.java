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
import de.egladil.web.mk_commons.dao.IUploadInfoDao;
import de.egladil.web.mk_commons.domain.impl.UploadInfo;

/**
 * UploadInfoDaoTest
 */
class UploadInfoDaoTest extends AbstractIntegrationTest {

	private IUploadInfoDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new UploadInfoDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<UploadInfo> trefferliste = dao.load(UploadInfo.class);

		// Assert
		assertEquals(100, trefferliste.size());
	}

}
