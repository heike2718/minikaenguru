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
import de.egladil.web.mk_commons.dao.IPacemakerDao;
import de.egladil.web.mk_commons.domain.impl.Pacemaker;

/**
 * PacemakerDaoTest
 */
class PacemakerDaoTest extends AbstractIntegrationTest {

	private IPacemakerDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new PacemakerDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<Pacemaker> trefferliste = dao.load(Pacemaker.class);

		// Assert
		assertEquals(1, trefferliste.size());
	}

}
