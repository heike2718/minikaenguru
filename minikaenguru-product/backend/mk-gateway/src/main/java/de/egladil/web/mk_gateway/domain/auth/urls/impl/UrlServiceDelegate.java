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
import de.egladil.web.mk_gateway.domain.auth.client.IClientAccessTokenService;

/**
 * UrlServiceDelegate stellt Methoden zur Verfügung, die für das Authentisieren des Clients beim authprovider und das Ermitteln der
 * korrekten Browser-Redirect-Urls genutzt werden können.
 */
@ApplicationScoped
public class UrlServiceDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(UrlServiceDelegate.class);

	@ConfigProperty(name = "auth-app.url")
	String authAppUrl;

	@Inject
	IClientAccessTokenService clientAccessTokenService;

	/**
	 * Authentisiert den Client beim authprovider und gibt die login-Url für den Browser-Redirect zurück.
	 *
	 * @param  veranstalterClientId
	 * @param  veranstalterClientSecret
	 * @param  loginRedirectUrl
	 * @return                  Resposne
	 */
	public Response getLoginUrl(final String clientId, final String clientSecret, final String loginRedirectUrl) {

		String accessToken = orderTheClientAccessToken(clientId, clientSecret);

		if (StringUtils.isBlank(accessToken)) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		String redirectUrl = authAppUrl + "#/login?accessToken=" + accessToken + "&state=login&nonce=null&redirectUrl="
			+ loginRedirectUrl;

		LOG.debug(redirectUrl);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(redirectUrl))).build();

	}

	/**
	 * @return
	 */
	String orderTheClientAccessToken(final String clientId, final String clientSecret) {

		return clientAccessTokenService.orderAccessToken(clientId, clientSecret);
	}

	String getAuthAppUrl() {

		return authAppUrl;
	}

}
