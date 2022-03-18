// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.session.loginlogout.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.auth.AuthMode;
import de.egladil.web.mk_gateway.domain.auth.AuthResult;
import de.egladil.web.mk_gateway.domain.auth.session.MkSessionService;
import de.egladil.web.mk_gateway.domain.auth.session.Session;
import de.egladil.web.mk_gateway.domain.auth.session.SessionUtils;
import de.egladil.web.mk_gateway.domain.auth.session.loginlogout.LoginLogoutService;
import de.egladil.web.mk_gateway.domain.auth.session.tokens.TokenExchangeService;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;

/**
 * LoginLogoutServiceImpl
 */
@ApplicationScoped
public class LoginLogoutServiceImpl implements LoginLogoutService {

	private static final String SESSION_COOKIE_NAME = MkGatewayApp.CLIENT_COOKIE_PREFIX
		+ CommonHttpUtils.NAME_SESSIONID_COOKIE;

	private static final Logger LOG = LoggerFactory.getLogger(LoginLogoutServiceImpl.class);

	@ConfigProperty(name = "stage")
	String stage;

	@ConfigProperty(name = "mk-admin-app.client-id")
	String adminClientId;

	@ConfigProperty(name = "mk-admin-app.client-secret")
	String adminClientSecret;

	@ConfigProperty(name = "mkv-app.client-id")
	String veranstalterClientId;

	@ConfigProperty(name = "mkv-app.client-secret")
	String veranstalterClientSecret;

	@Inject
	MkSessionService sessionService;

	@Inject
	TokenExchangeService tokenExchangeService;

	@Inject
	DomainEventHandler domainEventHandler;

	@Override
	public Response login(final AuthResult authResult, final AuthMode authMode) {

		if (authResult == null) {

			String msg = "login wurde ohne payload aufgerufen";

			new LoggableEventDelegate().fireSecurityEvent(msg, domainEventHandler);

			throw new BadRequestException("erwarte payload");
		}

		final String oneTimeToken = authResult.getIdToken();

		LOG.debug("idToken={}", StringUtils.abbreviate(oneTimeToken, 11));

		String jwt = this.tokenExchangeService.exchangeTheOneTimeToken(clientId(authMode), clientSecret(authMode), oneTimeToken);

		/*
		 * Generierung eines lang laufenden Tokens für Tests: Doku siehe xwiki/wiki/heikeswiki/view/01%20Development/FAQ/
		 * System.err.println("==================");
		 * System.out.println(jwt);
		 * System.err.println("==================");
		 */

		Session session = sessionService.initSession(jwt);

		NewCookie sessionCookie = SessionUtils.createSessionCookie(SESSION_COOKIE_NAME, session.sessionId());

		if (!MkGatewayApp.STAGE_DEV.equals(stage)) {

			// TODO: schauen, ob dies aufgerufen wird.
			session.clearSessionId();
		}

		ResponsePayload payload = new ResponsePayload(MessagePayload.info("OK"), session);
		return Response.ok(payload).cookie(sessionCookie).build();
	}

	@Override
	public Response logout(final String sessionId) {

		if (sessionId != null) {

			sessionService.invalidateSession(sessionId);
		} else {

			LOG.info("sessionId was null");
		}

		return Response.ok().entity(ResponsePayload.messageOnly(MessagePayload.info("Sie haben sich erfolreich ausgeloggt")))
			.cookie(CommonHttpUtils.createSessionInvalidatedCookie(SESSION_COOKIE_NAME)).build();
	}

	@Override
	public Response logoutDev(final String sessionId) {

		if (sessionId != null) {

			sessionService.invalidateSession(sessionId);
		} else {

			LOG.info("sessionId was null");
		}

		if (!MkGatewayApp.STAGE_DEV.equals(stage)) {

			String msg = "logoutDev wurde auf der Umgebung " + stage + " aufgerufen. sessionId=" + sessionId;

			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, domainEventHandler);

			return Response.status(401)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("böse böse. Dieser Request wurde geloggt!")))
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(SESSION_COOKIE_NAME)).build();
		}

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("Sie haben sich erfolreich ausgeloggt")))
			.cookie(CommonHttpUtils.createSessionInvalidatedCookie(SESSION_COOKIE_NAME)).build();
	}

	private String clientId(final AuthMode authMode) {

		if (authMode == AuthMode.ADMIN) {

			return this.adminClientId;
		}
		return this.veranstalterClientId;
	}

	private String clientSecret(final AuthMode authMode) {

		if (authMode == AuthMode.ADMIN) {

			return this.adminClientSecret;
		}
		return this.veranstalterClientSecret;
	}

}
