// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.signup;

import java.util.function.Function;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.mk_gateway.domain.DecodedJWTReader;
import de.egladil.web.mk_gateway.domain.auth.AuthResult;
import de.egladil.web.mk_gateway.domain.auth.session.SessionUtils;
import de.egladil.web.mk_gateway.domain.auth.session.tokens.TokenExchangeService;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.error.LogmessagePrefixes;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;

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
	DomainEventHandler domainEventHandler;

	@Inject
	LoggableEventDelegate eventDelegate;

	AuthResultToResourceOwnerMapper() {

		super();

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
			DecodedJWTReader decodedJWTReader = new DecodedJWTReader(decodedJWT);
			String fullName = decodedJWTReader.getFullName();
			String email = null;
			Claim emailClaim = decodedJWT.getClaim("email");

			if (emailClaim != null) {

				email = emailClaim.asString();
			}

			if (StringUtils.isBlank(fullName)) {

				throw new MkGatewayRuntimeException(
					"Fehler in der Konfiguration der MinikänguruApp beim AuthProvider: Vor- und Nachname sind erforderlich");
			}

			System.out.println(authResult.getNonce());

			return new SignUpResourceOwner(uuid, fullName, email, authResult.getNonce());

		} catch (TokenExpiredException e) {

			LOG.error("JWT expired");
			throw new AuthException(e.getMessage());
		} catch (JWTVerificationException e) {

			String msg = LogmessagePrefixes.BOT + "JWT " + StringUtils.abbreviate(jwt, 20) + " invalid: " + e.getMessage();

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);

			LOG.warn(msg);
			throw new AuthException("invalid JWT");
		}
	}
}
