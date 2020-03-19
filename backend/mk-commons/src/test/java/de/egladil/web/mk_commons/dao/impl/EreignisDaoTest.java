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
import de.egladil.web.mk_commons.dao.IEreignisDao;
import de.egladil.web.mk_commons.domain.impl.Ereignis;

/**
 * EreignisDaoTest
 */
class EreignisDaoTest extends AbstractIntegrationTest {

	private IEreignisDao dao;

	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		dao = new EreignisDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<Ereignis> trefferliste = dao.load(Ereignis.class);

		// Assert
		assertEquals(379, trefferliste.size());
	}

}
