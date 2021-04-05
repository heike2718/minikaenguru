// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UserHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * UserHibernateRepositoryIT
 */
public class UserHibernateRepositoryIT extends AbstractIntegrationTest {

	private UserRepository userRepository;

	@BeforeEach
	void init() {

		super.setUp();
		userRepository = UserHibernateRepository.createForIntegrationTest(entityManager);
	}

	@Test
	void should_findUserById() {

		// Arrange
		String userID = "it-db-inside-docker";

		// Act
		Optional<User> optUser = userRepository.ofId(userID);

		// Assert
		assertTrue(optUser.isPresent());
		User user = optUser.get();
		assertEquals(Rolle.ADMIN, user.getRolle());
		assertEquals(userID, user.getUuid());

	}

}
