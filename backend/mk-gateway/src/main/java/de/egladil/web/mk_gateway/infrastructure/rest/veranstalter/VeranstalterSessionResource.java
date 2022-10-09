// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

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
import javax.ws.rs.core.Response;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.auth.AuthMode;
import de.egladil.web.mk_gateway.domain.auth.AuthResult;
import de.egladil.web.mk_gateway.domain.auth.session.loginlogout.LoginLogoutService;
import de.egladil.web.mk_gateway.domain.auth.urls.AuthLoginSignupUrlService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * VeranstalterSessionResource ist der Endpoint für mkv-app, um sich ein- und auszuloggen.
 */
@RequestScoped
@Path("veranstalter/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VeranstalterSessionResource {

	private static final String SESSION_COOKIE_NAME = MkGatewayApp.CLIENT_COOKIE_PREFIX
		+ CommonHttpUtils.NAME_SESSIONID_COOKIE;

	@Inject
	AuthLoginSignupUrlService authUrlService;

	@Inject
	LoginLogoutService loginLogoutService;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("authurls/login")
	public Response getLoginUrl() {

		return authUrlService.getLoginUrl();

	}

	@GET
	@Path("authurls/signup/lehrer/{schulkuerzel}/{newsletterAbonnieren}")
	public Response getSignupLehrerUrl(@PathParam(value = "schulkuerzel") final String schulkuerzel, @PathParam(
		value = "newsletterAbonnieren") final String newsletterAbonnieren) {

		this.delayService.pause();

		return authUrlService.getSignupLehrerUrl(schulkuerzel, newsletterAbonnieren);
	}

	@GET
	@Path("authurls/signup/privat/{newsletterAbonnieren}")
	public Response getSignupPrivatmenschUrl(@PathParam(
		value = "newsletterAbonnieren") final String newsletterAbonnieren) {

		this.delayService.pause();

		return authUrlService.getSignupPrivatUrl(newsletterAbonnieren);
	}

	@POST
	@Path("login")
	public Response login(final AuthResult authResult) {

		this.delayService.pause();

		return loginLogoutService.login(authResult, AuthMode.VERANSTALTER);
	}

	@DELETE
	@Path("logout")
	public Response logout(@CookieParam(value = SESSION_COOKIE_NAME) final String sessionId) {

		this.delayService.pause();

		return loginLogoutService.logout(sessionId);
	}

	@DELETE
	@Path("dev/logout/{sessionId}")
	public Response logoutDev(@PathParam(value = "sessionId") final String sessionId) {

		this.delayService.pause();
		return loginLogoutService.logoutDev(sessionId);
	}
}
