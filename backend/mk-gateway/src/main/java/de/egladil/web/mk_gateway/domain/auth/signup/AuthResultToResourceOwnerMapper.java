// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.signup;

import java.util.function.Function;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.mk_gateway.domain.DecodedJWTReader;
import de.egladil.web.mk_gateway.domain.auth.AuthResult;
import de.egladil.web.mk_gateway.domain.auth.session.SessionUtils;
import de.egladil.web.mk_gateway.domain.auth.session.tokens.TokenExchangeService;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.error.LogmessagePrefixes;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;

/**
 * AuthResultToResourceOwnerMapper
 */
@RequestScoped
public class AuthResultToResourceOwnerMapper implements Function<AuthResult, SignUpResourceOwner> {

	private static final Logger LOG = LoggerFactory.getLogger(AuthResultToResourceOwnerMapper.class);

	@ConfigProperty(name = "mkv-app.client-id")
	String clientId;

	@ConfigProperty(name = "mkv-app.client-secret")
	String clientSecret;

	@Inject
	TokenExchangeService tokenExchangeService;

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
	public static AuthResultToResourceOwnerMapper createForTest(final JWTService jwtService, final TokenExchangeService tokenExchangeService, final String clientId, final String clientSecret) {

		if (jwtService == null) {

			throw new IllegalArgumentException("jwtService darf nicht null sein");
		}

		AuthResultToResourceOwnerMapper result = new AuthResultToResourceOwnerMapper();

		result.jwtService = jwtService;
		result.tokenExchangeService = tokenExchangeService;
		result.clientId = clientId;
		result.clientSecret = clientSecret;

		return result;
	}

	@Override
	public SignUpResourceOwner apply(final AuthResult authResult) {

		if (authResult == null) {

			throw new IllegalArgumentException("authResult darf nicht null sein");
		}

		String oneTimeToken = authResult.getIdToken();
		String jwt = this.tokenExchangeService.exchangeTheOneTimeToken(clientId, clientSecret, oneTimeToken);

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

			String msg = LogmessagePrefixes.BOT + "JWT " + StringUtils.abbreviate(jwt, 20) + " invalid: " + e.getMessage();

			this.securityIncident = new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);

			LOG.warn(msg);
			throw new AuthException("invalid JWT");
		}
	}

	public SecurityIncidentRegistered getSecurityIncident() {

		return securityIncident;
	}
}
