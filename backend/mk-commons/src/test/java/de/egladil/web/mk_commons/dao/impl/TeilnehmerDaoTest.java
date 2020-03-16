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
import de.egladil.web.mk_commons.dao.ITeilnehmerDao;
import de.egladil.web.mk_commons.domain.impl.Teilnehmer;

/**
 * TeilnehmerDaoTest
 */
class TeilnehmerDaoTest extends AbstractIntegrationTest {

	private ITeilnehmerDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new TeilnehmerDao(entityManager);

	}

	@Test
	void should_Load() {

		// Act
		List<Teilnehmer> trefferliste = dao.load(Teilnehmer.class);

		// Assert
		assertEquals(107, trefferliste.size());
	}

}
