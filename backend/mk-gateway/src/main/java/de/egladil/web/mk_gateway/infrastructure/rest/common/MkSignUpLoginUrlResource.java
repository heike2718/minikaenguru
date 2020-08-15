// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.common;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.infrastructure.clientauth.IClientAccessTokenService;

/**
 * MkSignUpLoginUrlResource stellt für die mkv-app die Login-und Signup-Urls zur Verfügung.
 */
@RequestScoped
@Path("/authurls")
@Produces(MediaType.APPLICATION_JSON)
public class MkSignUpLoginUrlResource {

	private static final Logger LOG = LoggerFactory.getLogger(MkSignUpLoginUrlResource.class);

	@ConfigProperty(name = "stage")
	String stage;

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
	@Path("/login")
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
	@Path("/signup/lehrer/{schulkuerzel}/{newsletterAbonnieren}")
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
	@Path("/signup/privat/{newsletterAbonnieren}")
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

}
