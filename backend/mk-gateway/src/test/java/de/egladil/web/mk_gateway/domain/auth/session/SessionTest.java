// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.auth.session.LoggedInUser;
import de.egladil.web.mk_gateway.domain.auth.session.Session;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * SessionTest
 */
public class SessionTest {

	@Test
	void should_CreateAnonymousSessionSetSessionId_when_everythingIsFine() {

		// Arrange
		final String sessionId = "nsdlhq";
		final long expiresAt = System.currentTimeMillis();

		// Act
		Session session = Session.createAnonymous(sessionId);

		// Assert
		assertEquals(sessionId, session.sessionId());
		assertNull(session.user());
		assertEquals(0l, session.getExpiresAt());

		session.setExpiresAt(expiresAt);
		assertEquals(expiresAt, session.getExpiresAt());

		assertEquals(session, session);
		assertFalse(session.equals(null));
		assertFalse(session.equals(new Object()));

	}

	@Test
	void should_EqualsBeCorrectlyImplemented() {

		// Arrange
		Session session1 = Session.createAnonymous("sdhgqk");
		Session session2 = Session.create("sdhgqk", LoggedInUser.create("asdq", Rolle.PRIVAT, "jwgdwq", "sös"));

		// Assert
		assertEquals(session1, session2);
		assertEquals(session1.hashCode(), session2.hashCode());

	}

	@Test
	void should_CreateSessionSetSessionId_when_everythingIsFine() {

		// Arrange
		final String sessionId = "nsdlhq";
		final long expiresAt = System.currentTimeMillis();
		LoggedInUser user = LoggedInUser.create("sqgq", Rolle.LEHRER, "Heinz", "snkq");

		// Act
		Session session = Session.create(sessionId, user);

		// Assert
		assertEquals(sessionId, session.sessionId());
		assertEquals(user, session.user());
		assertEquals(0l, session.getExpiresAt());

		session.setExpiresAt(expiresAt);
		assertEquals(expiresAt, session.getExpiresAt());

		assertEquals(session, session);
		assertFalse(session.equals(null));
		assertFalse(session.equals(new Object()));

		session.clearSessionId();
		assertNull(session.sessionId());

	}

	@Test
	void should_CreateAnonymousSessionThrowEqxecption_when_sessionIdNull() {

		// Act
		try {

			Session.createAnonymous(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("sessionId darf nicht blank sein", e.getMessage());
		}

	}

	@Test
	void should_CreateAnonymousSessionThrowEqxecption_when_sessionIdBlank() {

		// Act
		try {

			Session.createAnonymous(" ");
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("sessionId darf nicht blank sein", e.getMessage());
		}

	}

	@Test
	void should_CreateSessionThrowEqxecption_when_sessionIdNull() {

		// Act
		try {

			Session.create(null, LoggedInUser.create("sghdags", Rolle.ADMIN, "root", "kdJ"));
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("sessionId darf nicht blank sein", e.getMessage());
		}

	}

	@Test
	void should_CreateSessionThrowEqxecption_when_userNull() {

		// Act
		try {

			Session.create("asvhdfq", null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("user darf nicht null sein", e.getMessage());
		}

	}

}
