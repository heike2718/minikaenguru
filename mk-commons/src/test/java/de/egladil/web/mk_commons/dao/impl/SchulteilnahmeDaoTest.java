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
import de.egladil.web.mk_commons.dao.ISchulteilnahmeDao;
import de.egladil.web.mk_commons.domain.impl.Schulteilnahme;

/**
 * SchulteilnahmeDaoTest
 */
class SchulteilnahmeDaoTest extends AbstractIntegrationTest {

	private ISchulteilnahmeDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new SchulteilnahmeDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<Schulteilnahme> trefferliste = dao.load(Schulteilnahme.class);

		// Assert
		assertEquals(381, trefferliste.size());
	}

}
