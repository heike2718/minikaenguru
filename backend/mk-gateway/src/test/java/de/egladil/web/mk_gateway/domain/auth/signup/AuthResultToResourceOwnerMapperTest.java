// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.signup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.commons_crypto.impl.JWTServiceImpl;
import de.egladil.web.mk_gateway.domain.auth.AuthResult;
import de.egladil.web.mk_gateway.domain.auth.session.tokens.TokenExchangeService;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;

/**
 * AuthResultToResourceOwnerMapperTest
 */
public class AuthResultToResourceOwnerMapperTest {

	private static final String CLIENT_ID = "hallo";

	private static final String CLIENT_SECRET = "Welt";

	private JWTService jwtService = new JWTServiceImpl();

	@Test
	void should_ConstructorThrowException_when_JWTServiceNull() {

		try {

			AuthResultToResourceOwnerMapper.createForTest(null, Mockito.mock(TokenExchangeService.class), CLIENT_ID, CLIENT_SECRET);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("jwtService darf nicht null sein", e.getMessage());
		}

	}

	@Test
	void should_ApplyThrowException_when_ParameterNull() {

		// Arrange
		AuthResultToResourceOwnerMapper mapper = AuthResultToResourceOwnerMapper.createForTest(jwtService,
			Mockito.mock(TokenExchangeService.class), CLIENT_ID, CLIENT_SECRET);

		// Act
		try {

			mapper.apply(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("authResult darf nicht null sein", e.getMessage());
			assertNull(mapper.getSecurityIncident());
		}
	}

	@Test
	void should_ApplyThrowException_when_FullNameNull() throws IOException {

		// Arrange
		TokenExchangeService tokenExchangeService = Mockito.mock(TokenExchangeService.class);
		AuthResultToResourceOwnerMapper mapper = AuthResultToResourceOwnerMapper.createForTest(jwtService,
			tokenExchangeService, CLIENT_ID, CLIENT_SECRET);

		String idToken = "ashqiidhpi";

		String jwt = null;

		try (InputStream in = AuthResultToResourceOwnerMapperTest.class.getResourceAsStream("/long-lasting-jwt-without-name.txt");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName(MkGatewayFileUtils.DEFAULT_ENCODING));
			jwt = sw.toString();
		}

		assertNotNull(jwt);

		AuthResult authResult = new AuthResult();
		authResult.setIdToken(idToken);

		Mockito.when(tokenExchangeService.exchangeTheOneTimeToken(CLIENT_ID, CLIENT_SECRET, idToken)).thenReturn(jwt);

		// Act
		try {

			mapper.apply(authResult);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Fehler in der Konfiguration der MinikänguruApp beim AuthProvider: Vor- und Nachname sind erforderlich",
				e.getMessage());
			assertNull(mapper.getSecurityIncident());
		}
	}

	@Test
	void should_ApplyWork() throws IOException {

		// Arrange
		TokenExchangeService tokenExchangeService = Mockito.mock(TokenExchangeService.class);
		AuthResultToResourceOwnerMapper mapper = AuthResultToResourceOwnerMapper.createForTest(jwtService,
			tokenExchangeService, CLIENT_ID, CLIENT_SECRET);

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

		Mockito.when(tokenExchangeService.exchangeTheOneTimeToken(CLIENT_ID, CLIENT_SECRET, idToken)).thenReturn(jwt);

		// Act
		SignUpResourceOwner result = mapper.apply(authResult);

		// Assert
		assertEquals("4d8ed03a-575a-442e-89f4-0e54e51dd0d8", result.uuid());
		assertEquals("Max Mustermann", result.fullName());
		assertNull(mapper.getSecurityIncident());
	}

	@Test
	void should_ApplyThrowException_when_TokenExpired() throws IOException {

		// Arrange
		TokenExchangeService tokenExchangeService = Mockito.mock(TokenExchangeService.class);
		AuthResultToResourceOwnerMapper mapper = AuthResultToResourceOwnerMapper.createForTest(jwtService,
			tokenExchangeService, CLIENT_ID, CLIENT_SECRET);

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

		Mockito.when(tokenExchangeService.exchangeTheOneTimeToken(CLIENT_ID, CLIENT_SECRET, idToken))
			.thenReturn(expiredJwt);

		try {

			mapper.apply(authResult);
			fail("keine AuthException");
		} catch (AuthException e) {

			assertEquals("The Token has expired on Tue Jun 30 06:46:57 CEST 2020.", e.getMessage());
			assertNull(mapper.getSecurityIncident());
		}

	}

	@Test
	void should_ApplyThrowException_when_TokenInvalid() throws IOException {

		// Arrange
		TokenExchangeService tokenExchangeService = Mockito.mock(TokenExchangeService.class);
		AuthResultToResourceOwnerMapper mapper = AuthResultToResourceOwnerMapper.createForTest(jwtService,
			tokenExchangeService, CLIENT_ID, CLIENT_SECRET);

		String expiredJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9";

		Mockito.when(tokenExchangeService.exchangeTheOneTimeToken(CLIENT_ID, CLIENT_SECRET, expiredJwt))
			.thenReturn(expiredJwt);

		AuthResult authResult = new AuthResult();
		authResult.setIdToken(expiredJwt);
		authResult.setNonce("LEHRER-26TZ54HE");

		try {

			mapper.apply(authResult);
			fail("keine AuthException");
		} catch (AuthException e) {

			assertEquals("invalid JWT", e.getMessage());
			assertNotNull(mapper.getSecurityIncident());
			assertEquals(
				"Possible BOT Attack: JWT eyJ0eXAiOiJKV1QiL... invalid: The token was expected to have 3 parts, but got 1.",
				mapper.getSecurityIncident().message());
		}

	}

}
