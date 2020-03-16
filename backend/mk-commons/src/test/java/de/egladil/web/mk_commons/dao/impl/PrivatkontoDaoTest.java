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
import de.egladil.web.mk_commons.dao.IPrivatkontoDao;
import de.egladil.web.mk_commons.domain.impl.Privatkonto;

/**
 * PrivatkontoDaoTest
 */
class PrivatkontoDaoTest extends AbstractIntegrationTest {

	private IPrivatkontoDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new PrivatkontoDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<Privatkonto> trefferliste = dao.load(Privatkonto.class);

		// Assert
		assertEquals(123, trefferliste.size());
	}

}
