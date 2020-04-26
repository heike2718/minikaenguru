// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * MkvApiSessionServiceTest
 */
public class MkvApiSessionServiceTest {

	@Test
	void should_InitSessionWork() throws Exception {

		// Arrange
		try (InputStream in = getClass().getResourceAsStream("/long-lasting-jwt-with-name.txt");
			StringWriter sw = new StringWriter()) {

			String uuid = "4d8ed03a-575a-442e-89f4-0e54e51dd0d8";
			IOUtils.copy(in, sw, "UTF-8");
			String jwt = sw.toString();

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid(uuid);

			UserRepository userRepository = Mockito.mock(UserRepository.class);
			Mockito.when(userRepository.ofId(uuid)).thenReturn(Optional.of(user));

			MkvApiSessionService service = MkvApiSessionService.createForTestWithUserRepository(userRepository);

			// Act
			Session session = service.initSession(jwt);

			// Assert
			assertNotNull(session);
			assertFalse(session.sessionId().isBlank());

			LoggedInUser loggedInUser = session.user();
			assertEquals(uuid, loggedInUser.getName());
			assertEquals(Rolle.PRIVAT, loggedInUser.rolle());
			assertEquals("Max Mustermann", loggedInUser.fullName());

		}

	}

	@Test
	void should_InvalidateSessionWork() throws Exception {

		// Arrange
		try (InputStream in = getClass().getResourceAsStream("/long-lasting-jwt-with-name.txt");
			StringWriter sw = new StringWriter()) {

			String uuid = "4d8ed03a-575a-442e-89f4-0e54e51dd0d8";
			IOUtils.copy(in, sw, "UTF-8");
			String jwt = sw.toString();

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid(uuid);

			UserRepository userRepository = Mockito.mock(UserRepository.class);
			Mockito.when(userRepository.ofId(uuid)).thenReturn(Optional.of(user));

			MkvApiSessionService service = MkvApiSessionService.createForTestWithUserRepository(userRepository);

			Session session = service.initSession(jwt);

			// Act
			service.invalidateSession(session.sessionId());

			// Assert
			assertNull(service.getAndRefreshSessionIfValid(session.sessionId()));

		}

	}

	@Test
	void should_InitSessionThrowAuthException_when_JwtExpired() throws Exception {

		// Arrange
		try (InputStream in = getClass().getResourceAsStream("/expired-jwt.txt");
			StringWriter sw = new StringWriter()) {

			String uuid = "4d8ed03a-575a-442e-89f4-0e54e51dd0d8";
			IOUtils.copy(in, sw, "UTF-8");
			String jwt = sw.toString();

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid(uuid);

			UserRepository userRepository = Mockito.mock(UserRepository.class);
			Mockito.when(userRepository.ofId(uuid)).thenReturn(Optional.of(user));

			MkvApiSessionService service = MkvApiSessionService.createForTestWithUserRepository(userRepository);

			// Act
			try {

				service.initSession(jwt);
				fail("keine AuthException");
			} catch (AuthException e) {

				assertEquals("JWT expired", e.getMessage());
			}
		}

	}

	@Test
	void should_InitSessionThrowAuthException_when_JwtInvalid() throws Exception {

		// Arrange
		String uuid = "4d8ed03a-575a-442e-89f4-0e54e51dd0d8";
		String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9";

		User user = new User();
		user.setRolle(Rolle.PRIVAT);
		user.setUuid(uuid);

		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.ofId(uuid)).thenReturn(Optional.of(user));

		MkvApiSessionService service = MkvApiSessionService.createForTestWithUserRepository(userRepository);

		// Act
		try {

			service.initSession(jwt);
			fail("keine AuthException");
		} catch (AuthException e) {

			assertEquals("JWT invalid", e.getMessage());
		}

	}

	@Test
	void should_InitSessionThrowException_when_UUIDUnknown() throws Exception {

		// Arrange
		try (InputStream in = getClass().getResourceAsStream("/long-lasting-jwt-with-name.txt");
			StringWriter sw = new StringWriter()) {

			String uuid = "4d8ed03a-575a-442e-89f4-0e54e51dd0d8";
			IOUtils.copy(in, sw, "UTF-8");
			String jwt = sw.toString();

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid(uuid);

			UserRepository userRepository = Mockito.mock(UserRepository.class);
			Mockito.when(userRepository.ofId(uuid)).thenReturn(Optional.empty());

			MkvApiSessionService service = MkvApiSessionService.createForTestWithUserRepository(userRepository);

			// Act
			try {

				service.initSession(jwt);
				fail("keine AuthException");
			} catch (AuthException e) {

				assertEquals("USER mit UUID 4d8ed03a-575a-442e-89f4-0e54e51dd0d8 existiert nicht", e.getMessage());
			}

		}

	}

	@Test
	void should_GetAndRefreshSessionIfValidWork() throws Exception {

		// Arrange
		try (InputStream in = getClass().getResourceAsStream("/long-lasting-jwt-with-name.txt");
			StringWriter sw = new StringWriter()) {

			String uuid = "4d8ed03a-575a-442e-89f4-0e54e51dd0d8";
			IOUtils.copy(in, sw, "UTF-8");
			String jwt = sw.toString();

			User user = new User();
			user.setRolle(Rolle.PRIVAT);
			user.setUuid(uuid);

			UserRepository userRepository = Mockito.mock(UserRepository.class);
			Mockito.when(userRepository.ofId(uuid)).thenReturn(Optional.of(user));

			MkvApiSessionService service = MkvApiSessionService.createForTestWithUserRepository(userRepository);

			Session session = service.initSession(jwt);

			long expiresAt = session.getExpiresAt();

			TimeUnit.MILLISECONDS.sleep(200);

			// Act
			Session refreshedSession = service.getAndRefreshSessionIfValid(session.sessionId());

			// Assert
			assertEquals(session, refreshedSession);
			assertEquals(session.user(), refreshedSession.user());
			assertTrue(refreshedSession.getExpiresAt() > expiresAt);

		}
	}

	@Test
	void should_GetAndRefreshSessionReturnNull_when_NotPresent() throws Exception {

		// Act + Assert
		MkvApiSessionService service = MkvApiSessionService.createForTestWithUserRepository(null);
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

		MkvApiSessionService service = MkvApiSessionService.createForTestWithSession(session);

		// Act
		try {

			service.getAndRefreshSessionIfValid(sessionId);
			fail("keine SessionExpiredException");
		} catch (SessionExpiredException e) {

			assertEquals("Ihre Session ist abgelaufen. Bitte loggen Sie sich erneut ein.", e.getMessage());
		}

	}

}
