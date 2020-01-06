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
import de.egladil.web.mk_commons.dao.IOrtDao;
import de.egladil.web.mk_commons.domain.impl.Ort;

/**
 * OrtDaoTest
 */
class OrtDaoTest extends AbstractIntegrationTest {

	private IOrtDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new OrtDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<Ort> trefferliste = dao.load(Ort.class);

		// Assert
		assertEquals(6727, trefferliste.size());
	}

}
