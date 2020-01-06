// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_commons.dao.AbstractIntegrationTest;
import de.egladil.web.mk_commons.dao.IAdvTextDao;
import de.egladil.web.mk_commons.domain.impl.AdvText;

/**
 * AdvTextDaoIT
 */
class AdvTextDaoIT extends AbstractIntegrationTest {

	private IAdvTextDao dao;

	@BeforeEach
	void setUp() throws Exception {

		try {

			initEntityManager();
			this.dao = new AdvTextDao(entityManager);
		} catch (Exception e) {

			e.printStackTrace();
			fail("klappt nicht");
		}
	}

	@Test
	void should_Load() {

		// Arrange
		int expectedNumber = 2;

		// Act
		List<AdvText> trefferliste = dao.load(AdvText.class);

		// Assert
		assertEquals(expectedNumber, trefferliste.size());
	}

}
