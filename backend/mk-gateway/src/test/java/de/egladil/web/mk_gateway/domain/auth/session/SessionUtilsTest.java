// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import javax.ws.rs.core.NewCookie;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.auth.session.SessionUtils;

/**
 * SessionUtilsTest
 */
public class SessionUtilsTest {

	@Test
	void should_CreateIdRefernceReturnNonEmptyString() {

		assertFalse(SessionUtils.createIdReference().isBlank());
	}

	@Test
	void should_GetExpriesAtGiveFutureTimestamp() {

		// Arrange
		long expectedMinimum = CommonTimeUtils.now().plus(3l, ChronoUnit.MINUTES).atZone(ZoneId.systemDefault()).toEpochSecond()
			* 1000;

		// Act
		long actual = SessionUtils.getExpiresAt(3);

		// Assert
		assertTrue(actual >= expectedMinimum);

	}

	@Test
	void should_GetPublicKeyWork() {

		assertTrue(
			new String(SessionUtils.getPublicKey()).contains("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwT4s/a90Wst40aAReBhm"));
	}

	@Test
	void should_CreateSessionCookieWork() {

		// Arrange
		String sessionId = "hallo";

		// Act
		NewCookie cookie = SessionUtils.createSessionCookie("sessionID", sessionId);

		// Assert
		assertNotNull(cookie);
		assertEquals(sessionId, cookie.getValue());
		assertTrue(cookie.isHttpOnly());
		assertTrue(cookie.isSecure());
		assertEquals(7200, cookie.getMaxAge());

	}

}
