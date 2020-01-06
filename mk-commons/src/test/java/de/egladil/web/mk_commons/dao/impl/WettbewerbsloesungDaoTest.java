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
import de.egladil.web.mk_commons.dao.IWettbewerbsloesungDao;
import de.egladil.web.mk_commons.domain.impl.Wettbewerbsloesung;

/**
 * WettbewerbsloesungDaoTest
 */
class WettbewerbsloesungDaoTest extends AbstractIntegrationTest {

	private IWettbewerbsloesungDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new WettbewerbsloesungDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<Wettbewerbsloesung> trefferliste = dao.load(Wettbewerbsloesung.class);

		// Assert
		assertEquals(18, trefferliste.size());
	}

}
