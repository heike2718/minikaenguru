// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.users;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.session.MkvApiSessionService;
import de.egladil.web.mk_gateway.domain.session.Session;
import de.egladil.web.mk_gateway.domain.session.SessionUtils;
import de.egladil.web.mk_gateway.domain.signup.AuthResult;

/**
 * SessionResource ist der Endpoint für den Minikänguru-Microservice-Zoo, um sich ein- und auszuloggen.
 */
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

	private static final String SESSION_COOKIE_NAME = MkGatewayApp.CLIENT_COOKIE_PREFIX
		+ CommonHttpUtils.NAME_SESSIONID_COOKIE;

	private static final Logger LOG = LoggerFactory.getLogger(SessionResource.class);

	@ConfigProperty(name = "stage")
	String stage;

	@Inject
	MkvApiSessionService sessionService;

	@POST
	@Path("/login")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response login(final AuthResult authResult) {

		final String jwt = authResult.getIdToken();

		Session session = sessionService.initSession(jwt);

		NewCookie sessionCookie = SessionUtils.createSessionCookie(SESSION_COOKIE_NAME, session.sessionId());

		if (!MkGatewayApp.STAGE_DEV.equals(stage)) {

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

		if (!MkGatewayApp.STAGE_DEV.equals(stage)) {

			LOG.warn("URL wurde auf Umgebung {} aufgerufen, sessionId=", stage, sessionId);
			return Response.status(401)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("böse böse. Dieser Request wurde geloggt!")))
				.cookie(CommonHttpUtils.createSessionInvalidatedCookie(SESSION_COOKIE_NAME)).build();
		}

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("Sie haben sich erfolreich ausgeloggt")))
			.cookie(CommonHttpUtils.createSessionInvalidatedCookie(SESSION_COOKIE_NAME)).build();
	}
}
