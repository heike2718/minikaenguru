// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.mk_gateway.domain.auth.events.UserLoggedIn;
import de.egladil.web.mk_gateway.domain.auth.events.UserLoggedOut;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * MkSessionServiceTest
 */
@QuarkusTest
public class MkSessionServiceTest {

	@InjectMock
	private JWTService jwtService;

	@InjectMock
	private UserRepository userRepository;

	@InjectMock
	private CryptoService cryptoService;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@Inject
	private MkSessionService service;

	void setUp() {

	}

	@Test
	void should_InitSessionWork() throws Exception {

		// Arrange
		try (InputStream in = getClass().getResourceAsStream("/long-lasting-jwt-with-name.txt");
			StringWriter sw = new StringWriter()) {

			String uuid = "4d8ed03a-575a-442e-89f4-0e54e51dd0d8";
			IOUtils.copy(in, sw, Charset.forName(MkGatewayFileUtils.DEFAULT_ENCODING));
			String jwt = sw.toString();

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid(uuid);

			when(userRepository.ofId(uuid)).thenReturn(Optional.of(user));

			DecodedJWT decodedJWT = Mockito.mock(DecodedJWT.class);
			Claim fullName = Mockito.mock(Claim.class);

			when(fullName.asString()).thenReturn("Max Mustermann");
			when(decodedJWT.getSubject()).thenReturn(uuid);
			when(decodedJWT.getClaim(any())).thenReturn(fullName);

			when(jwtService.verify(any(), any())).thenReturn(decodedJWT);

			when(cryptoService.generateSessionId()).thenReturn("Hallo Hallo");

			// Act
			Session session = service.initSession(jwt);

			// Assert
			assertNotNull(session);
			assertFalse(session.sessionId().isBlank());

			LoggedInUser loggedInUser = session.user();
			assertEquals(uuid, loggedInUser.getName());
			assertEquals(Rolle.PRIVAT, loggedInUser.rolle());
			assertEquals("Max Mustermann", loggedInUser.fullName());
			assertTrue(loggedInUser.idReference().contains("_4d8ed03a"));

			String serialized = new ObjectMapper().writeValueAsString(session);
			assertTrue(serialized.contains("sessionId"));
			assertTrue(serialized.contains("expiresAt"));
			assertTrue(serialized.contains("user"));
			assertTrue(serialized.contains("idReference"));
			assertTrue(serialized.contains("rolle"));
			assertTrue(serialized.contains("fullName"));

			// System.out.println(serialized);
			verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			verify(domainEventHandler, never()).handleEvent(any(UserLoggedOut.class));
			verify(domainEventHandler).handleEvent(any(UserLoggedIn.class));
		}
	}

	@Test
	void should_InvalidateSessionWork() throws Exception {

		// Arrange
		String uuid = "4d8ed03a-575a-442e-89f4-0e54e51dd0d8";
		String jwt = "gudguqguogoq-qhwodhoqhoh";

		User user = new User();
		user.setRolle(Rolle.PRIVAT);
		user.setUuid(uuid);

		when(userRepository.ofId(uuid)).thenReturn(Optional.of(user));

		DecodedJWT decodedJWT = Mockito.mock(DecodedJWT.class);
		Claim fullName = Mockito.mock(Claim.class);

		when(fullName.asString()).thenReturn("Horst");
		when(decodedJWT.getSubject()).thenReturn(uuid);
		when(decodedJWT.getClaim(any())).thenReturn(fullName);

		when(jwtService.verify(any(), any())).thenReturn(decodedJWT);

		when(cryptoService.generateSessionId()).thenReturn("Hallo Hallo");

		Session session = service.initSession(jwt);
		verify(domainEventHandler).handleEvent(any(UserLoggedIn.class));

		// Act
		service.invalidateSession(session.sessionId());

		// Assert
		verify(eventDelegate, never()).fireSecurityEvent(any(), any());
		verify(domainEventHandler).handleEvent(any(UserLoggedOut.class));
		// nur bei initSession, nicht ein zweites Mal!
		verify(domainEventHandler).handleEvent(any(UserLoggedIn.class));

		assertNull(service.getAndRefreshSessionIfValid(session.sessionId()));
	}

	@Test
	void should_InvalidateSessionIgnore_when_SessionNull() throws Exception {

		// Arrange
		String sessionToken = "Hallo";

		// Act
		service.invalidateSession(sessionToken);

		// Assert
		assertNull(service.getAndRefreshSessionIfValid(sessionToken));
		verify(eventDelegate, never()).fireSecurityEvent(any(), any());
		verify(domainEventHandler, never()).handleEvent(any(UserLoggedOut.class));
	}

	@Test
	void should_InitSessionThrowAuthException_when_JwtExpired() throws Exception {

		String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9";

		when(jwtService.verify(any(), any())).thenThrow(new TokenExpiredException("hallo", Instant.now()));

		// Act
		try {

			service.initSession(jwt);
			fail("keine AuthException");
		} catch (AuthException e) {

			assertEquals("JWT expired", e.getMessage());

			verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			verify(domainEventHandler, never()).handleEvent(any(UserLoggedOut.class));
			verify(domainEventHandler, never()).handleEvent(any(UserLoggedIn.class));

			verify(jwtService, times(1)).verify(any(), any());
			verify(userRepository, times(0)).ofId(any());
			verify(cryptoService, times(0)).generateSessionId();
		}

	}

	@Test
	void should_InitSessionThrowAuthException_when_JwtInvalid() throws Exception {

		// Arrange
		String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9";

		when(jwtService.verify(any(), any())).thenThrow(new JWTVerificationException("ungültig"));

		// Act
		try {

			service.initSession(jwt);
			fail("keine AuthException");
		} catch (AuthException e) {

			assertEquals("JWT invalid", e.getMessage());
			verify(eventDelegate).fireSecurityEvent(any(), any());
			verify(domainEventHandler, never()).handleEvent(any(UserLoggedOut.class));
			verify(domainEventHandler, never()).handleEvent(any(UserLoggedIn.class));
			verify(jwtService, times(1)).verify(any(), any());
			verify(userRepository, times(0)).ofId(any());
			verify(cryptoService, times(0)).generateSessionId();
		}

	}

	@Test
	void should_InitSessionThrowException_when_UUIDUnknown() throws Exception {

		// Arrange
		try (InputStream in = getClass().getResourceAsStream("/long-lasting-jwt-with-name.txt");
			StringWriter sw = new StringWriter()) {

			String uuid = "4d8ed03a-575a-442e-89f4-0e54e51dd0d8";
			IOUtils.copy(in, sw, Charset.forName(MkGatewayFileUtils.DEFAULT_ENCODING));
			String jwt = sw.toString();

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid(uuid);

			when(userRepository.ofId(any())).thenReturn(Optional.empty());

			DecodedJWT decodedJWT = Mockito.mock(DecodedJWT.class);
			Claim fullName = Mockito.mock(Claim.class);

			when(fullName.asString()).thenReturn("Horst");
			when(decodedJWT.getSubject()).thenReturn(uuid);
			when(decodedJWT.getClaim(any())).thenReturn(fullName);

			when(jwtService.verify(any(), any())).thenReturn(decodedJWT);

			// Act
			try {

				service.initSession(jwt);
				fail("keine AuthException");
			} catch (AuthException e) {

				assertEquals("USER mit UUID 4d8ed03a-575a-442e-89f4-0e54e51dd0d8 existiert nicht", e.getMessage());
				verify(eventDelegate).fireSecurityEvent(any(), any());
				verify(domainEventHandler, never()).handleEvent(any(UserLoggedOut.class));
				verify(domainEventHandler, never()).handleEvent(any(UserLoggedIn.class));

				verify(jwtService, times(1)).verify(any(), any());
				verify(cryptoService, times(0)).generateSessionId();
			}

		}

	}

	@Test
	void should_GetAndRefreshSessionIfValidWork() throws Exception {

		// Arrange
		String uuid = "4d8ed03a-575a-442e-89f4-0e54e51dd0d8";
		String jwt = "ajkhfhqiohfiqhhfqhoh";

		User user = new User();
		user.setRolle(Rolle.PRIVAT);
		user.setUuid(uuid);

		when(userRepository.ofId(uuid)).thenReturn(Optional.of(user));

		DecodedJWT decodedJWT = Mockito.mock(DecodedJWT.class);
		Claim fullName = Mockito.mock(Claim.class);

		when(fullName.asString()).thenReturn("Horst");
		when(decodedJWT.getSubject()).thenReturn(uuid);
		when(decodedJWT.getClaim(any())).thenReturn(fullName);

		when(jwtService.verify(any(), any())).thenReturn(decodedJWT);

		when(cryptoService.generateSessionId()).thenReturn("Hallo Hallo");

		Session session = service.initSession(jwt);
		verify(domainEventHandler).handleEvent(any(UserLoggedIn.class));

		long expiresAt = session.getExpiresAt();

		TimeUnit.MILLISECONDS.sleep(200);

		// Act
		Session refreshedSession = service.getAndRefreshSessionIfValid(session.sessionId());

		// Assert
		assertEquals(session, refreshedSession);
		assertEquals(session.user(), refreshedSession.user());
		assertTrue(refreshedSession.getExpiresAt() > expiresAt);
		verify(eventDelegate, never()).fireSecurityEvent(any(), any());
		verify(domainEventHandler, never()).handleEvent(any(UserLoggedOut.class));

		// nur bei initSession, nicht ein zweites Mal!
		verify(domainEventHandler).handleEvent(any(UserLoggedIn.class));
	}

	@Test
	void should_GetAndRefreshSessionReturnNull_when_NotPresent() throws Exception {

		// Act + Assert
		assertNull(service.getAndRefreshSessionIfValid("hallo"));
	}

	@Test
	void should_InvalidateThrowException_when_SessionExpired() throws Exception {

		// Arrange
		String sessionId = "Hallo";
		LoggedInUser user = LoggedInUser.create("adgq", Rolle.LEHRER, "Heinz", "xsaqh");

		Session session = Session.create(sessionId, user);
		session.setExpiresAt(System.currentTimeMillis());

		TimeUnit.MILLISECONDS.sleep(200);

		service.initSessionForTest(session);

		// Act
		try {

			service.getAndRefreshSessionIfValid(sessionId);
			fail("keine SessionExpiredException");
		} catch (SessionExpiredException e) {

			assertEquals("Ihre Session ist abgelaufen. Bitte loggen Sie sich erneut ein.", e.getMessage());
		}

	}

}
