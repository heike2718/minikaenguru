// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.signup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.mk_gateway.domain.auth.AuthResult;
import de.egladil.web.mk_gateway.domain.auth.session.tokens.TokenExchangeService;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * AuthResultToResourceOwnerMapperTest
 */
@QuarkusTest
public class AuthResultToResourceOwnerMapperTest {

	private static final String CLIENT_ID = "hallo";

	private static final String CLIENT_SECRET = "Welt";

	@InjectMock
	JWTService jwtService;

	@InjectMock
	TokenExchangeService tokenExchangeService;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@Inject
	AuthResultToResourceOwnerMapper mapper;

	@Test
	void should_ApplyThrowException_when_ParameterNull() {

		// Arrange
		// Act
		try {

			mapper.apply(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("authResult darf nicht null sein", e.getMessage());
			verify(eventDelegate).fireSecurityEvent(any(), any());
		}
	}

	// @Test: haben leider kein gültiges JWT ohne Namen mehr, da authprovider Client.isVornameNachnameRequired() ignoriert.
	void should_ApplyThrowException_when_FullNameNull() throws IOException {

		// Arrange
		String idToken = "ashqiidhpi";

		String jwt = null;

		try (InputStream in = AuthResultToResourceOwnerMapperTest.class.getResourceAsStream("/long-lasting-jwt-with-name.txt");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName(MkGatewayFileUtils.DEFAULT_ENCODING));
			jwt = sw.toString();
		}

		assertNotNull(jwt);

		AuthResult authResult = new AuthResult();
		authResult.setIdToken(idToken);
		authResult.setNonce("LEHRER-26TZ54HE");

		when(tokenExchangeService.exchangeTheOneTimeToken(CLIENT_ID, CLIENT_SECRET, idToken)).thenReturn(jwt);

		// Act
		try {

			mapper.apply(authResult);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Fehler in der Konfiguration der MinikänguruApp beim AuthProvider: Vor- und Nachname sind erforderlich",
				e.getMessage());

			verify(eventDelegate, never()).fireSecurityEvent(any(), any());
		}
	}

	@Test
	void should_ApplyWork() throws IOException {

		// Arrange
		String idToken = "ashqiidhpi";

		String jwt = null;

		try (InputStream in = AuthResultToResourceOwnerMapperTest.class.getResourceAsStream("/long-lasting-jwt-with-name.txt");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName(MkGatewayFileUtils.DEFAULT_ENCODING));
			jwt = sw.toString();
		}

		assertNotNull(jwt);

		AuthResult authResult = new AuthResult();
		authResult.setIdToken(idToken);
		authResult.setNonce("LEHRER-26TZ54HE");

		when(tokenExchangeService.exchangeTheOneTimeToken(CLIENT_ID, CLIENT_SECRET, idToken)).thenReturn(jwt);

		// Act
		SignUpResourceOwner result = mapper.apply(authResult);

		// Assert
		assertEquals("412b67dc-132f-465a-a3c3-468269e866cb", result.uuid());
		assertEquals("Frodo Beutlin aus Beutelsend", result.fullName());
		verify(eventDelegate, never()).fireSecurityEvent(any(), any());
	}

	@Test
	void should_ApplyThrowException_when_TokenExpired() throws IOException {

		// Arrange
		String idToken = "ashqiidhpi";

		String expiredJwt = null;

		try (InputStream in = getClass().getResourceAsStream("/expired-jwt.txt");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName(MkGatewayFileUtils.DEFAULT_ENCODING));
			expiredJwt = sw.toString();
		}

		assertNotNull(expiredJwt);

		AuthResult authResult = new AuthResult();
		authResult.setIdToken(idToken);
		authResult.setNonce("LEHRER-26TZ54HE");

		when(tokenExchangeService.exchangeTheOneTimeToken(CLIENT_ID, CLIENT_SECRET, idToken))
			.thenReturn(expiredJwt);

		try {

			mapper.apply(authResult);
			fail("keine AuthException");
		} catch (AuthException e) {

			assertEquals("The Token has expired on Tue Jun 30 06:46:57 CEST 2020.", e.getMessage());
			verify(eventDelegate, never()).fireSecurityEvent(any(), any());
		}

	}

	@Test
	void should_ApplyThrowException_when_TokenInvalid() throws IOException {

		// Arrange
		String expiredJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9";

		when(tokenExchangeService.exchangeTheOneTimeToken(CLIENT_ID, CLIENT_SECRET, expiredJwt))
			.thenReturn(expiredJwt);

		AuthResult authResult = new AuthResult();
		authResult.setIdToken(expiredJwt);
		authResult.setNonce("LEHRER-26TZ54HE");

		try {

			mapper.apply(authResult);
			fail("keine AuthException");
		} catch (AuthException e) {

			assertEquals("invalid JWT", e.getMessage());
			verify(eventDelegate).fireSecurityEvent(any(), any());
		}

	}

}
