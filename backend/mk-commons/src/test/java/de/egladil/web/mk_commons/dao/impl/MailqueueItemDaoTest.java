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
import de.egladil.web.mk_commons.dao.IMailqueueItemDao;
import de.egladil.web.mk_commons.domain.impl.MailqueueItem;

/**
 * MailqueueItemDaoTest
 */
class MailqueueItemDaoTest extends AbstractIntegrationTest {

	private IMailqueueItemDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new MailqueueItemDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<MailqueueItem> trefferliste = dao.load(MailqueueItem.class);

		// Assert
		assertEquals(0, trefferliste.size());
	}

}
