// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.admin.AdminTokenExchangeService;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.session.MkSessionService;
import de.egladil.web.mk_gateway.domain.session.Session;
import de.egladil.web.mk_gateway.domain.session.SessionUtils;
import de.egladil.web.mk_gateway.domain.signup.AuthResult;

/**
 * AdminSessionResource ist der Endpoint für mk-admin-app, um sich einzuloggen.
 */
@RequestScoped
@Path("/wb-admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminSessionResource {

	private static final String SESSION_COOKIE_NAME = MkGatewayApp.CLIENT_COOKIE_PREFIX
		+ CommonHttpUtils.NAME_SESSIONID_COOKIE;

	private static final Logger LOG = LoggerFactory.getLogger(AdminSessionResource.class);

	@ConfigProperty(name = "stage")
	String stage;

	@Inject
	MkSessionService sessionService;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	@Inject
	AdminTokenExchangeService tokenExchangeService;

	@POST
	@Path("/login")
	public Response loginAsAdmin(final AuthResult authResult) {

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
