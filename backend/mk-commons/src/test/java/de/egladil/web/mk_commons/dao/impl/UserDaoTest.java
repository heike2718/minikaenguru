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
import de.egladil.web.mk_commons.dao.IUserDao;
import de.egladil.web.mk_commons.domain.impl.User;

/**
 * UserDaoTest
 */
class UserDaoTest extends AbstractIntegrationTest {

	private IUserDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initEntityManager();
		this.dao = new UserDao(entityManager);
	}

	@Test
	void should_Load() {

		// Act
		List<User> trefferliste = dao.load(User.class);

		// Assert
		assertEquals(2, trefferliste.size());
	}

}
