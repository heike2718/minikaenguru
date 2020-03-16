// =====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.clientauth.impl;

import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.OAuthClientCredentials;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_commons.clientauth.IClientAccessTokenService;
import de.egladil.web.mk_commons.clientauth.OrderAccessTokenRestClient;
import de.egladil.web.mk_commons.exception.ClientAuthException;
import de.egladil.web.mk_commons.exception.LogmessagePrefixes;
import de.egladil.web.mk_commons.exception.MkRuntimeException;

/**
 * ClientAccessTokenService
 */
@RequestScoped
public class ClientAccessTokenService implements IClientAccessTokenService {

	private static final Logger LOG = LoggerFactory.getLogger(ClientAccessTokenService.class);

	@ConfigProperty(name = "auth.client-id")
	String clientId;

	@ConfigProperty(name = "auth.client-secret")
	String clientSecret;

	@Inject
	@RestClient
	OrderAccessTokenRestClient orderAccessTokenClient;

	/**
	 * Holt ein clientAccessToken vom authprovider
	 *
	 * @return String das accessToken oder null!
	 */
	@Override
	public String orderAccessToken() {

		String nonce = UUID.randomUUID().toString();
		OAuthClientCredentials credentials = OAuthClientCredentials.create(clientId, clientSecret, nonce);

		try {

			Response authResponse = orderAccessTokenClient.authenticateClient(credentials);

			ResponsePayload responsePayload = authResponse.readEntity(ResponsePayload.class);

			evaluateResponse(nonce, responsePayload);

			@SuppressWarnings("unchecked")
			Map<String, String> dataMap = (Map<String, String>) responsePayload.getData();
			String accessToken = dataMap.get("accessToken");

			return accessToken;
		} catch (IllegalStateException | RestClientDefinitionException | WebApplicationException e) {

			String msg = "Unerwarteter Fehler beim Anfordern eines client-accessTokens: " + e.getMessage();
			LOG.error(msg, e);
			throw new MkRuntimeException(msg, e);
		} catch (ClientAuthException e) {

			// wurde schon geloggt
			return null;
		}
	}

	private void evaluateResponse(final String nonce, final ResponsePayload responsePayload) throws ClientAuthException {

		MessagePayload messagePayload = responsePayload.getMessage();

		if (messagePayload.isOk()) {

			@SuppressWarnings("unchecked")
			Map<String, String> dataMap = (Map<String, String>) responsePayload.getData();
			String responseNonce = dataMap.get("nonce");

			if (!nonce.equals(responseNonce)) {

				LOG.warn(LogmessagePrefixes.BOT + "zurückgesendetes nonce stimmt nicht");
				throw new ClientAuthException();
			}
		} else {

			LOG.error("Authentisierung des Clients hat nicht geklappt: {} - {}", messagePayload.getLevel(),
				messagePayload.getMessage());
			throw new ClientAuthException();
		}
	}
}
