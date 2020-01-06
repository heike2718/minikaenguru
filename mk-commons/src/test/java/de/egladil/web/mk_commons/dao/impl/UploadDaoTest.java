// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_commons.IUploadDao;
import de.egladil.web.mk_commons.dao.AbstractIntegrationTest;
import de.egladil.web.mk_commons.domain.impl.Upload;

/**
 * UploadDaoTest
 */
class UploadDaoTest extends AbstractIntegrationTest {

	private IUploadDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new UploadDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<Upload> trefferliste = dao.load(Upload.class);

		// Assert
		assertEquals(103, trefferliste.size());
	}

}
