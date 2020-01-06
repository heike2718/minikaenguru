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
import de.egladil.web.mk_commons.dao.ILehrerkontoDao;
import de.egladil.web.mk_commons.domain.impl.Lehrerkonto;

/**
 * LehrerkontoDaoTest
 */
class LehrerkontoDaoTest extends AbstractIntegrationTest {

	private ILehrerkontoDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new LehrerkontoDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<Lehrerkonto> trefferliste = dao.load(Lehrerkonto.class);

		// Assert
		assertEquals(171, trefferliste.size());
	}

}
