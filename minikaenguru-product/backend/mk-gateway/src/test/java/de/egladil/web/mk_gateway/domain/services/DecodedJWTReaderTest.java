// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.commons_crypto.impl.JWTServiceImpl;
import de.egladil.web.mk_gateway.domain.DecodedJWTReader;
import de.egladil.web.mk_gateway.domain.auth.session.SessionUtils;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;

/**
 * DecodedJWTReaderTest
 */
public class DecodedJWTReaderTest {

	private String jwtWithName;

	private String jwtWithoutName;

	private JWTService jwtService;

	private byte[] publicKey;

	@BeforeEach
	void setUp() throws Exception {

		this.jwtService = new JWTServiceImpl();
		this.publicKey = SessionUtils.getPublicKey();

		try (InputStream in = getClass().getResourceAsStream("/long-lasting-jwt-with-name.txt");
			StringWriter sw = new StringWriter()) {

			// Arrange
			IOUtils.copy(in, sw, Charset.forName(MkGatewayFileUtils.DEFAULT_ENCODING));
			jwtWithName = sw.toString();
		}

		try (InputStream in = getClass().getResourceAsStream("/long-lasting-jwt-without-name.txt");
			StringWriter sw = new StringWriter()) {

			// Arrange
			IOUtils.copy(in, sw, Charset.forName(MkGatewayFileUtils.DEFAULT_ENCODING));
			jwtWithoutName = sw.toString();
		}
	}

	@Test
	void should_GetFullNameReturnTheName_when_isPresent() {

		// Arrange
		DecodedJWT decodedJWT = jwtService.verify(jwtWithName, publicKey);
		DecodedJWTReader reader = new DecodedJWTReader(decodedJWT);

		// Act
		String fullName = reader.getFullName();

		// Assert
		assertEquals("Frodo Beutlin aus Beutelsend", fullName);

	}

	// TODO: eher Test für Checklistenapp
	// @Test
	void should_GetFullNameReturnNull_when_isNotPresent() {

		// Arrange
		DecodedJWT decodedJWT = jwtService.verify(jwtWithoutName, publicKey);
		DecodedJWTReader reader = new DecodedJWTReader(decodedJWT);

		// Act
		String fullName = reader.getFullName();

		// Assert
		assertNull(fullName);
	}

	@Test
	void should_ConstructorThrowException_when_DecodedJWTNull() {

		try {

			new DecodedJWTReader(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("decodedJWT darf nicht null sein.", e.getMessage());
		}

	}
}
