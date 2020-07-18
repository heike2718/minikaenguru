// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.infrastructure.clientauth.IClientAccessTokenService;

/**
 * MkAdminLoginUrlResource stellt für die mk-admin-app die Login zur Verfügung.
 */
@RequestScoped
@Path("/wb-admin/authurls")
@Produces(MediaType.APPLICATION_JSON)
public class MkAdminLoginUrlResource {

	private static final Logger LOG = LoggerFactory.getLogger(MkAdminLoginUrlResource.class);

	@ConfigProperty(name = "stage")
	String stage;

	@ConfigProperty(name = "mk-admin-app.client-id")
	String mkAdminAppClientId;

	@ConfigProperty(name = "mk-admin-app.client-secret")
	String mkAdminAppClientSecret;

	@ConfigProperty(name = "auth-app.url")
	String authAppUrl;

	@ConfigProperty(name = "mk-admin-app.redirect-url.login")
	String adminLoginRedirectUrl;

	@Inject
	IClientAccessTokenService clientAccessTokenService;

	@GET
	@Path("/login")
	public Response getAdminLoginUrl() {

		String accessToken = clientAccessTokenService.orderAccessToken(mkAdminAppClientId, mkAdminAppClientSecret);

		if (StringUtils.isBlank(accessToken)) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		String redirectUrl = authAppUrl + "#/login?accessToken=" + accessToken + "&state=login&nonce=null&redirectUrl="
			+ adminLoginRedirectUrl;

		LOG.debug(redirectUrl);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(redirectUrl))).build();

	}
}
