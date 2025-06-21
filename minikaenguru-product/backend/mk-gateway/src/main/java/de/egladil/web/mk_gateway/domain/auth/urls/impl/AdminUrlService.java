// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.urls.impl;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.mk_gateway.domain.auth.urls.AuthLoginUrlService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

/**
 * AdminUrlService
 */
@ApplicationScoped
public class AdminUrlService implements AuthLoginUrlService {

	@ConfigProperty(name = "mk-admin-app.client-id")
	String clientId;

	@ConfigProperty(name = "mk-admin-app.client-secret")
	String clientSecret;

	@ConfigProperty(name = "mk-admin-app.redirect-url.login")
	String loginRedirectUrl;

	@Inject
	UrlServiceDelegate serviceDelegate;

	@Override
	public Response getLoginUrl() {

		return serviceDelegate.getLoginUrl(clientId, clientSecret, loginRedirectUrl);
	}

}
