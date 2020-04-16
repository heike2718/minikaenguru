// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.mk_gateway.domain.services.DecodedJWTReader;
import de.egladil.web.mk_gateway.domain.session.SessionUtils;
import de.egladil.web.mk_gateway.error.AuthException;
import de.egladil.web.mk_gateway.error.LogmessagePrefixes;
import de.egladil.web.mk_gateway.error.MkGatewayRuntimeException;

/**
 * AuthResultToResourceOwnerMapper
 */
public class AuthResultToResourceOwnerMapper implements Function<AuthResult, SignUpResourceOwner> {

	private static final Logger LOG = LoggerFactory.getLogger(AuthResultToResourceOwnerMapper.class);

	private final JWTService jwtService;

	/**
	 * @param jwtService
	 */
	public AuthResultToResourceOwnerMapper(final JWTService jwtService) {

		if (jwtService == null) {

			throw new IllegalArgumentException("jwtService darf nicht null sein");
		}

		this.jwtService = jwtService;
	}

	@Override
	public SignUpResourceOwner apply(final AuthResult authResult) {

		if (authResult == null) {

			throw new IllegalArgumentException("authResult darf nicht null sein");
		}

		String jwt = authResult.getIdToken();

		try {

			DecodedJWT decodedJWT = jwtService.verify(jwt, SessionUtils.getPublicKey());

			String uuid = decodedJWT.getSubject();
			String fullName = new DecodedJWTReader(decodedJWT).getFullName();

			if (StringUtils.isBlank(fullName)) {

				throw new MkGatewayRuntimeException(
					"Fehler in der Konfiguration der MinikänguruApp beim AuthProvider: Vor- und Nachname sind erforderlich");
			}

			return new SignUpResourceOwner(uuid, fullName, authResult.getNonce());

		} catch (TokenExpiredException e) {

			LOG.error("JWT expired");
			throw new AuthException(e.getMessage());
		} catch (JWTVerificationException e) {

			LOG.warn(LogmessagePrefixes.BOT + "JWT invalid: {}", e.getMessage());
			throw new AuthException("invalid JWT");
		}
	}
}
