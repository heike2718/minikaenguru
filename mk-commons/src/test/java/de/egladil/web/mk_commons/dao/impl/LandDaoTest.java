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
import de.egladil.web.mk_commons.dao.ILandDao;
import de.egladil.web.mk_commons.domain.impl.Land;

/**
 * LandDaoTest
 */
class LandDaoTest extends AbstractIntegrationTest {

	private ILandDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		dao = new LandDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<Land> trefferliste = dao.load(Land.class);

		// Assert
		assertEquals(63, trefferliste.size());
	}

}
