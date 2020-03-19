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
import de.egladil.web.mk_commons.dao.IPrivatdownloadDao;
import de.egladil.web.mk_commons.domain.impl.Privatdownload;

/**
 * PrivatdownloadDaoTest
 */
class PrivatdownloadDaoTest extends AbstractIntegrationTest {

	private IPrivatdownloadDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new PrivatdownloadDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<Privatdownload> trefferliste = dao.load(Privatdownload.class);

		// Assert
		assertEquals(9, trefferliste.size());
	}

}
