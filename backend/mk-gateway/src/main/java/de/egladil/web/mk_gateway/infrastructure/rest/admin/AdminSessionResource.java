// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.auth.AuthMode;
import de.egladil.web.mk_gateway.domain.auth.AuthResult;
import de.egladil.web.mk_gateway.domain.auth.session.loginlogout.LoginLogoutService;
import de.egladil.web.mk_gateway.domain.auth.urls.AuthLoginUrlService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * AdminSessionResource ist der Endpoint für mk-admin-app, um sich einzuloggen.
 */
@RequestScoped
@Path("admin/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminSessionResource {

	private static final String SESSION_COOKIE_NAME = MkGatewayApp.CLIENT_COOKIE_PREFIX
		+ CommonHttpUtils.NAME_SESSIONID_COOKIE;

	@Inject
	AuthLoginUrlService authLoginUrlService;

	@Inject
	LoginLogoutService loginLogoutService;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("authurls/login")
	public Response getAdminLoginUrl() {

		return authLoginUrlService.getLoginUrl();

	}

	@POST
	@Path("login")
	public Response login(final AuthResult authResult) {

		this.delayService.pause();

		return loginLogoutService.login(authResult, AuthMode.ADMIN);
	}

	@DELETE
	@Path("logout")
	public Response logout(@CookieParam(value = SESSION_COOKIE_NAME) final String sessionId) {

		this.delayService.pause();
		return this.loginLogoutService.logout(sessionId);
	}

	@DELETE
	@Path("dev/logout/{sessionId}")
	public Response logoutDev(@PathParam(value = "sessionId") final String sessionId) {

		this.delayService.pause();

		return this.loginLogoutService.logoutDev(sessionId);
	}
}
