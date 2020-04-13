// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.Principal;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.model.Rolle;

/**
 * MkvSecurityContextTest
 */
public class MkvSecurityContextTest {

	@Test
	void should_ConstructorThrowException_when_SessionNull() {

		try {

			new MkvSecurityContext(null, false);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("session darf nicht null sein", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_SessionAnonymous() {

		// Arrange
		Session session = Session.createAnonymous("yxhicaho");

		try {

			new MkvSecurityContext(session, false);
			fail("keine IllegalStateException");
		} catch (IllegalStateException e) {

			assertEquals("session darf nicht anonym sein", e.getMessage());
		}

	}

	@Test
	void should_GetUserPrincipal_ReturnTheUser() {

		// Arrange
		LoggedInUser user = LoggedInUser.create("heinziheinzi", Rolle.PRIVAT, "Heinzi Heinz", "hashdo");
		Session session = Session.create("asjkcg", user);

		MkvSecurityContext securityContext = new MkvSecurityContext(session, true);

		// Act
		Principal principal = securityContext.getUserPrincipal();

		// Assert
		assertEquals(user, principal);
		assertEquals("Bearer", securityContext.getAuthenticationScheme());
		assertTrue(securityContext.isSecure());

	}

	@Test
	void should_IsUserInRole_ReturnFalse_when_OtherRole() {

		// Arrange
		LoggedInUser user = LoggedInUser.create("heinziheinzi", Rolle.PRIVAT, "Heinzi Heinz", "hashdo");
		Session session = Session.create("asjkcg", user);

		MkvSecurityContext securityContext = new MkvSecurityContext(session, false);

		// Act
		boolean userInRole = securityContext.isUserInRole("ADMIN");

		// Assert
		assertFalse(userInRole);

	}

	@Test
	void should_IsUserInRole_ReturnTrue_when_Role() {

		// Arrange
		LoggedInUser user = LoggedInUser.create("heinziheinzi", Rolle.PRIVAT, "Heinzi Heinz", "hashdo");
		Session session = Session.create("asjkcg", user);

		MkvSecurityContext securityContext = new MkvSecurityContext(session, false);

		// Act
		boolean userInRole = securityContext.isUserInRole("PRIVAT");

		// Assert
		assertTrue(userInRole);

	}

}
