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
import de.egladil.web.mk_commons.dao.IAdvVereinbarungDao;
import de.egladil.web.mk_commons.domain.impl.AdvVereinbarung;

/**
 * AdvVereinbarungDaoTest
 */
class AdvVereinbarungDaoTest extends AbstractIntegrationTest {

	private IAdvVereinbarungDao dao;

	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new AdvVereinbarungDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<AdvVereinbarung> trefferliste = dao.load(AdvVereinbarung.class);

		// Assert
		assertEquals(4, trefferliste.size());
	}

}
