// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.urls.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.auth.urls.AuthLoginSignupUrlService;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * VeranstalterUrlService
 */
@ApplicationScoped
public class VeranstalterUrlService implements AuthLoginSignupUrlService {

	private static final Logger LOG = LoggerFactory.getLogger(VeranstalterUrlService.class);

	@ConfigProperty(name = "mkv-app.client-id")
	String clientId;

	@ConfigProperty(name = "mkv-app.client-secret")
	String clientSecret;

	@ConfigProperty(name = "mkv-app.redirect-url.login")
	String loginRedirectUrl;

	@ConfigProperty(name = "mkv-app.redirect-url.signup")
	String signupRedirectUrl;

	@Inject
	UrlServiceDelegate serviceDelegate;

	@Override
	public Response getLoginUrl() {

		return serviceDelegate.getLoginUrl(clientId, clientSecret, loginRedirectUrl);
	}

	@Override
	public Response getSignupLehrerUrl(final String schulkuerzel, final String newsletterAbonnieren) {

		String accessToken = this.serviceDelegate.orderTheClientAccessToken(clientId, clientSecret);

		if (StringUtils.isBlank(accessToken)) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		String nonce = Rolle.LEHRER + "-" + schulkuerzel;

		boolean abonnieren = Boolean.valueOf(newsletterAbonnieren);

		if (abonnieren) {

			nonce += "-" + abonnieren;
		}

		String redirectUrl = serviceDelegate.getAuthAppUrl() + "#/signup?accessToken=" + accessToken + "&state=signup&nonce="
			+ nonce + "&redirectUrl="
			+ signupRedirectUrl;

		LOG.debug(redirectUrl);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(redirectUrl))).build();
	}

	@Override
	public Response getSignupPrivatUrl(final String newsletterAbonnieren) {

		String accessToken = this.serviceDelegate.orderTheClientAccessToken(clientId, clientSecret);

		if (StringUtils.isBlank(accessToken)) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		String nonce = Rolle.PRIVAT.name();

		boolean abonnieren = Boolean.valueOf(newsletterAbonnieren);

		if (abonnieren) {

			nonce += "-" + abonnieren;
		}

		String redirectUrl = serviceDelegate.getAuthAppUrl() + "#/signup?accessToken=" + accessToken + "&state=signup&nonce="
			+ nonce + "&redirectUrl="
			+ signupRedirectUrl;

		LOG.debug(redirectUrl);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(redirectUrl))).build();
	}
}
