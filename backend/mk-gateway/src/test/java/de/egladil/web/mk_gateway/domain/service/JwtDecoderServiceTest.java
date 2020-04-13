// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.impl.JWTServiceImpl;
import de.egladil.web.mk_gateway.domain.services.JwtDecoderService;

/**
 * JwtDecoderServiceTest
 */
public class JwtDecoderServiceTest {

	private String jwtWithName;

	private String jwtWithoutName;

	private String expiredJwt;

	private JwtDecoderService service = new JwtDecoderService();

	@BeforeEach
	void setUp() throws Exception {

		try (InputStream in = getClass().getResourceAsStream("/long-lasting-jwt-with-name.txt");
			StringWriter sw = new StringWriter()) {

			// Arrange
			IOUtils.copy(in, sw, "UTF-8");
			jwtWithName = sw.toString();
		}

		try (InputStream in = getClass().getResourceAsStream("/long-lasting-jwt-without-name.txt");
			StringWriter sw = new StringWriter()) {

			// Arrange
			IOUtils.copy(in, sw, "UTF-8");
			jwtWithoutName = sw.toString();
		}

		try (InputStream in = getClass().getResourceAsStream("/expired-jwt.txt");
			StringWriter sw = new StringWriter()) {

			// Arrange
			IOUtils.copy(in, sw, "UTF-8");
			expiredJwt = sw.toString();
		}
	}

	@Test
	void should_DecodeValidJwt() throws Exception {

		// Act
		DecodedJWT decodedJWT = service.decodeJwt(jwtWithName, new JWTServiceImpl());

		// Assert
		assertNotNull(decodedJWT);
		assertEquals("4d8ed03a-575a-442e-89f4-0e54e51dd0d8", decodedJWT.getSubject());

	}

	@Test
	void should_GetFullNameReturnTheName_when_isPresent() {

		// Arrange
		DecodedJWT decodedJWT = service.decodeJwt(jwtWithName, new JWTServiceImpl());

		// Act
		String fullName = service.getFullName(decodedJWT);

		// Assert
		assertEquals("Max Mustermann", fullName);

	}

	@Test
	void should_GetFullNameReturnNull_when_isNotPresent() {

		// Arrange
		DecodedJWT decodedJWT = service.decodeJwt(jwtWithoutName, new JWTServiceImpl());

		// Act
		String fullName = service.getFullName(decodedJWT);

		// Assert
		assertNull(fullName);
	}

	@Test
	void should_DecodeThrowException_when_NotValid() {

		try {

			service.decodeJwt("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9", new JWTServiceImpl());
			fail("keine JWTDecodeException");
		} catch (JWTDecodeException e) {

			assertEquals("The token was expected to have 3 parts, but got 1.", e.getMessage());
		}

	}

	@Test
	void should_DecodeThrowException_when_Expired() {

		try {

			service.decodeJwt(expiredJwt, new JWTServiceImpl());
			fail("keine TokenExpiredException");
		} catch (TokenExpiredException e) {

			assertEquals("The Token has expired on Mon Apr 13 15:02:32 CEST 2020.", e.getMessage());
		}

	}
}
