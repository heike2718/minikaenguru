// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.users;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.session.MkSessionService;
import de.egladil.web.mk_gateway.domain.session.Session;
import de.egladil.web.mk_gateway.domain.session.SessionUtils;
import de.egladil.web.mk_gateway.domain.session.TokenExchangeService;
import de.egladil.web.mk_gateway.domain.signup.AuthResult;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.infrastructure.clientauth.IClientAccessTokenService;

/**
 * SessionResource ist der Endpoint für mkv-app, um sich ein- und auszuloggen.
 */
@RequestScoped
@Path("/wettbewerb/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

	private static final String SESSION_COOKIE_NAME = MkGatewayApp.CLIENT_COOKIE_PREFIX
		+ CommonHttpUtils.NAME_SESSIONID_COOKIE;

	private static final Logger LOG = LoggerFactory.getLogger(SessionResource.class);

	@ConfigProperty(name = "stage")
	String stage;

	@Inject
	MkSessionService sessionService;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	@Inject
	TokenExchangeService tokenExchangeService;

	@ConfigProperty(name = "mkv-app.client-id")
	String mkvAppClientId;

	@ConfigProperty(name = "mkv-app.client-secret")
	String mkvAppClientSecret;

	@ConfigProperty(name = "auth-app.url")
	String authAppUrl;

	@ConfigProperty(name = "mkv-app.redirect-url.login")
	String loginRedirectUrl;

	@ConfigProperty(name = "mkv-app.redirect-url.signup")
	String signupRedirectUrl;

	@Inject
	IClientAccessTokenService clientAccessTokenService;

	@GET
	@Path("/authurls/login")
	public Response getLoginUrl() {

		String accessToken = clientAccessTokenService.orderAccessToken(mkvAppClientId, mkvAppClientSecret);

		if (StringUtils.isBlank(accessToken)) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		String redirectUrl = authAppUrl + "#/login?accessToken=" + accessToken + "&state=login&nonce=null&redirectUrl="
			+ loginRedirectUrl;

		LOG.debug(redirectUrl);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(redirectUrl))).build();

	}

	@GET
	@Path("/authurls/signup/lehrer/{schulkuerzel}/{newsletterAbonnieren}")
	public Response getSignupLehrerUrl(@PathParam(value = "schulkuerzel") final String schulkuerzel, @PathParam(
		value = "newsletterAbonnieren") final String newsletterAbonnieren) {

		String accessToken = clientAccessTokenService.orderAccessToken(mkvAppClientId, mkvAppClientSecret);

		if (StringUtils.isBlank(accessToken)) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		String nonce = Rolle.LEHRER + "-" + schulkuerzel;

		boolean abonnieren = Boolean.valueOf(newsletterAbonnieren);

		if (abonnieren) {

			nonce += "-" + abonnieren;
		}

		String redirectUrl = authAppUrl + "#/signup?accessToken=" + accessToken + "&state=signup&nonce=" + nonce + "&redirectUrl="
			+ signupRedirectUrl;

		LOG.debug(redirectUrl);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(redirectUrl))).build();
	}

	@GET
	@Path("/authurls/signup/privat/{newsletterAbonnieren}")
	public Response getSignupPrivatmenschUrl(@PathParam(
		value = "newsletterAbonnieren") final String newsletterAbonnieren) {

		String accessToken = clientAccessTokenService.orderAccessToken(mkvAppClientId, mkvAppClientSecret);

		if (StringUtils.isBlank(accessToken)) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		String nonce = Rolle.PRIVAT.name();

		boolean abonnieren = Boolean.valueOf(newsletterAbonnieren);

		if (abonnieren) {

			nonce += "-" + abonnieren;
		}

		String redirectUrl = authAppUrl + "#/signup?accessToken=" + accessToken + "&state=signup&nonce=" + nonce + "&redirectUrl="
			+ signupRedirectUrl;

		LOG.debug(redirectUrl);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(redirectUrl))).build();
	}

	@POST
	@Path("/login")
	public Response login(final AuthResult authResult) {

		if (authResult == null) {

			String msg = "login wurde ohne payload aufgerufen";

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);

			throw new BadRequestException("erwarte payload");
		}

		final String oneTimeToken = authResult.getIdToken();

		LOG.debug("idToken={}", StringUtils.abbreviate(oneTimeToken, 11));

		String jwt = this.tokenExchangeService.exchangeTheOneTimeToken(oneTimeToken);

		Session session = sessionService.initSession(jwt);

		NewCookie sessionCookie = SessionUtils.createSessionCookie(SESSION_COOKIE_NAME, session.sessionId());

		if (!MkGatewayApp.STAGE_DEV.equals(stage)) {

			// TODO: schauen, ob dies aufgerufen wird.
			session.clearSessionId();
		}

		ResponsePayload payload = new ResponsePayload(MessagePayload.info("OK"), session);
		return Response.ok(payload).cookie(sessionCookie).build();
	}

	@DELETE
	@Path("/logout")
	public Response logout(@CookieParam(value = SESSION_COOKIE_NAME) final String sessionId) {

		if (sessionId != null) {

			sessionService.invalidateSession(sessionId);
		} else {

			LOG.info("sessionId was null");
		}

		return Response.ok().entity(ResponsePayload.messageOnly(MessagePayload.info("Sie haben sich erfolreich ausgeloggt")))
			.cookie(CommonHttpUtils.createSessionInvalidatedCookie(SESSION_COOKIE_NAME)).build();
	}

	@DELETE
	@Path("/dev/logout/{sessionId}")
	public Response logoutDev(@PathParam(value = "sessionId") final String sessionId) {

		if (sessionId != null) {

			sessionService.invalidateSession(sessionId);
		} else {

			LOG.info("sessionId was null");
		}

		if (!MkGatewayApp.STAGE_DEV.equals(stage)) {

			String msg = "logoutDev wurde auf der Umgebung " + stage + " aufgerufen. sessionId=" + sessionId;

			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);

			return Response.status(401)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("böse böse. Dieser Request wurde geloggt!")))
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(SESSION_COOKIE_NAME)).build();
		}

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("Sie haben sich erfolreich ausgeloggt")))
			.cookie(CommonHttpUtils.createSessionInvalidatedCookie(SESSION_COOKIE_NAME)).build();
	}
}
