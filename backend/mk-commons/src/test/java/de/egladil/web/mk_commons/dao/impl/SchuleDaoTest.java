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
import de.egladil.web.mk_commons.dao.ISchuleDao;
import de.egladil.web.mk_commons.domain.impl.Schule;

/**
 * SchuleDaoTest
 */
class SchuleDaoTest extends AbstractIntegrationTest {

	private ISchuleDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new SchuleDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<Schule> trefferliste = dao.load(Schule.class);

		// Assert
		assertEquals(16352, trefferliste.size());
	}

}
