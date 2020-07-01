// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

import java.util.function.Function;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.mk_gateway.domain.DecodedJWTReader;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.error.LogmessagePrefixes;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.session.SessionUtils;

/**
 * AuthResultToResourceOwnerMapper
 */
@RequestScoped
public class AuthResultToResourceOwnerMapper implements Function<AuthResult, SignUpResourceOwner> {

	private static final Logger LOG = LoggerFactory.getLogger(AuthResultToResourceOwnerMapper.class);

	@Inject
	JWTService jwtService;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	private SecurityIncidentRegistered securityIncident;

	AuthResultToResourceOwnerMapper() {

		super();

	}

	/**
	 * @param jwtService
	 */
	public static AuthResultToResourceOwnerMapper createForTest(final JWTService jwtService) {

		if (jwtService == null) {

			throw new IllegalArgumentException("jwtService darf nicht null sein");
		}

		AuthResultToResourceOwnerMapper result = new AuthResultToResourceOwnerMapper();

		result.jwtService = jwtService;

		return result;
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

			String msg = LogmessagePrefixes.BOT + "JWT invalid: " + e.getMessage();

			this.securityIncident = new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);

			LOG.warn(msg);
			throw new AuthException("invalid JWT");
		}
	}

	public SecurityIncidentRegistered getSecurityIncident() {

		return securityIncident;
	}
}
