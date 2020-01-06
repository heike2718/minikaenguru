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
import de.egladil.web.mk_commons.dao.ILehrerdodwnloadDao;
import de.egladil.web.mk_commons.domain.impl.Lehrerdownload;

/**
 * LehrerdownloadDaoTest
 */
class LehrerdownloadDaoTest extends AbstractIntegrationTest {

	private ILehrerdodwnloadDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new LehrerdownloadDao(entityManager);

	}

	@Test
	void should_Load() {

		// Act
		List<Lehrerdownload> trefferliste = dao.load(Lehrerdownload.class);

		// Assert
		assertEquals(6, trefferliste.size());
	}
}
