// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.endpoints;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
import de.egladil.web.mk_commons.session.Session;
import de.egladil.web.mk_commons.session.SessionUtils;
import de.egladil.web.mkv_server.MkvServerApp;
import de.egladil.web.mkv_server.duplicated.clientauth.IMkvClientAccessTokenService;
import de.egladil.web.mkv_server.session.MkvSessionService;

/**
 * MkvSessionResource
 */
@RequestScoped
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class MkvSessionResource {

	private static final Logger LOG = LoggerFactory.getLogger(MkvSessionResource.class);

	private static final String SESSION_COOKIE_NAME = MkvServerApp.CLIENT_COOKIE_PREFIX + CommonHttpUtils.NAME_SESSIONID_COOKIE;

	@ConfigProperty(name = "stage")
	String stage;

	@ConfigProperty(name = "auth-app.url")
	String authAppUrl;

	@ConfigProperty(name = "auth.redirect-url.login")
	String loginRedirectUrl;

	@ConfigProperty(name = "auth.redirect-url.signup")
	String signupRedirectUrl;

	// wegen https://github.com/quarkusio/quarkus/pull/6411 muss ich leider ersteinmal alle CDIs hierher verschieben und mkvadmin so
	// lange auf Halde liegen lassen :(
	// @Inject
	// IClientAccessTokenService clientAccessTokenService;

	@Inject
	IMkvClientAccessTokenService clientAccessTokenService;

	@Inject
	MkvSessionService sessionService;

	@GET
	@Path("/login")
	@PermitAll
	public Response getLoginUrl() {

		String accessToken = clientAccessTokenService.orderAccessToken();

		if (StringUtils.isBlank(accessToken)) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		String redirectUrl = authAppUrl + "#/login?accessToken=" + accessToken + "&state=login&nonce=null&redirectUrl="
			+ loginRedirectUrl;

		LOG.debug(redirectUrl);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(redirectUrl))).build();

	}

	@POST
	@Path("/session")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response createSession(final String jwt) {

		Session session = sessionService.initSession(jwt);

		NewCookie sessionCookie = SessionUtils.createSessionCookie(SESSION_COOKIE_NAME, session.getSessionId());

		if (!MkvServerApp.STAGE_DEV.equals(stage)) {

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
		}

		return Response.ok().entity(ResponsePayload.messageOnly(MessagePayload.info("Sie haben sich erfolreich ausgeloggt")))
			.cookie(CommonHttpUtils.createSessionInvalidatedCookie(SESSION_COOKIE_NAME)).build();
	}

	@DELETE
	@Path("/dev/logout/{sessionid}")
	public Response logoutDev(@PathParam(value = "sessionId") final String sessionId) {

		if (sessionId != null) {

			sessionService.invalidateSession(sessionId);
		}

		if (!MkvServerApp.STAGE_DEV.equals(stage)) {

			LOG.warn("URL wurde auf Umgebung {} aufgerufen, sessionId=", stage, sessionId);
			return Response.status(401)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("böse böse. Dieser Request wurde geloggt!")))
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(SESSION_COOKIE_NAME)).build();
		}

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("Sie haben sich erfolreich ausgeloggt")))
			.cookie(CommonHttpUtils.createSessionInvalidatedCookie(SESSION_COOKIE_NAME)).build();
	}

}
