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
import de.egladil.web.mk_commons.dao.ILoesungszettelDao;
import de.egladil.web.mk_commons.domain.impl.Loesungszettel;

/**
 * LoesungszettelDaoTest
 */
class LoesungszettelDaoTest extends AbstractIntegrationTest {

	private ILoesungszettelDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new LoesungszettelDao(entityManager);

	}

	@Test
	void should_Load() {

		// Act
		List<Loesungszettel> trefferliste = dao.load(Loesungszettel.class);

		// Assert
		assertEquals(1424, trefferliste.size());
	}

}
