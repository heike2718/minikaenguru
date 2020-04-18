// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.commons_crypto.impl.JWTServiceImpl;
import de.egladil.web.mk_gateway.error.AuthException;
import de.egladil.web.mk_gateway.error.MkGatewayRuntimeException;

/**
 * AuthResultToResourceOwnerMapperTest
 */
public class AuthResultToResourceOwnerMapperTest {

	private JWTService jwtService = new JWTServiceImpl();

	@Test
	void should_ConstructorThrowException_when_JWTServiceNull() {

		try {

			new AuthResultToResourceOwnerMapper(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("jwtService darf nicht null sein", e.getMessage());
		}

	}

	@Test
	void should_ApplyThrowException_when_ParameterNull() {

		// Arrange
		AuthResultToResourceOwnerMapper mapper = new AuthResultToResourceOwnerMapper(jwtService);

		// Act
		try {

			mapper.apply(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("authResult darf nicht null sein", e.getMessage());
		}
	}

	@Test
	void should_ApplyThrowException_when_FullNameNull() throws IOException {

		// Arrange
		AuthResultToResourceOwnerMapper mapper = new AuthResultToResourceOwnerMapper(jwtService);

		String jwt = null;

		try (InputStream in = AuthResultToResourceOwnerMapperTest.class.getResourceAsStream("/long-lasting-jwt-without-name.txt");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName("UTF-8"));
			jwt = sw.toString();
		}

		assertNotNull(jwt);

		AuthResult authResult = new AuthResult();
		authResult.setIdToken(jwt);

		// Act
		try {

			mapper.apply(authResult);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Fehler in der Konfiguration der MinikänguruApp beim AuthProvider: Vor- und Nachname sind erforderlich",
				e.getMessage());
		}
	}

	@Test
	void should_ApplyWork() throws IOException {

		// Arrange
		AuthResultToResourceOwnerMapper mapper = new AuthResultToResourceOwnerMapper(jwtService);

		String jwt = null;

		try (InputStream in = AuthResultToResourceOwnerMapperTest.class.getResourceAsStream("/long-lasting-jwt-with-name.txt");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName("UTF-8"));
			jwt = sw.toString();
		}

		assertNotNull(jwt);

		AuthResult authResult = new AuthResult();
		authResult.setIdToken(jwt);
		authResult.setNonce("LEHRER-26TZ54HE");

		// Act
		SignUpResourceOwner result = mapper.apply(authResult);

		// Assert
		assertEquals("4d8ed03a-575a-442e-89f4-0e54e51dd0d8", result.uuid());
		assertEquals("Max Mustermann", result.fullName());
	}

	@Test
	void should_ApplyThrowException_when_TokenExpired() throws IOException {

		// Arrange
		AuthResultToResourceOwnerMapper mapper = new AuthResultToResourceOwnerMapper(jwtService);

		String expiredJwt = null;

		try (InputStream in = getClass().getResourceAsStream("/expired-jwt.txt");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, "UTF-8");
			expiredJwt = sw.toString();
		}

		assertNotNull(expiredJwt);

		AuthResult authResult = new AuthResult();
		authResult.setIdToken(expiredJwt);
		authResult.setNonce("LEHRER-26TZ54HE");

		try {

			mapper.apply(authResult);
			fail("keine AuthException");
		} catch (AuthException e) {

			assertEquals("The Token has expired on Mon Apr 13 15:02:32 CEST 2020.", e.getMessage());
		}

	}

	@Test
	void should_ApplyThrowException_when_TokenInvalid() throws IOException {

		// Arrange
		AuthResultToResourceOwnerMapper mapper = new AuthResultToResourceOwnerMapper(jwtService);

		String expiredJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9";

		AuthResult authResult = new AuthResult();
		authResult.setIdToken(expiredJwt);
		authResult.setNonce("LEHRER-26TZ54HE");

		try {

			mapper.apply(authResult);
			fail("keine AuthException");
		} catch (AuthException e) {

			assertEquals("invalid JWT", e.getMessage());
		}

	}

}
