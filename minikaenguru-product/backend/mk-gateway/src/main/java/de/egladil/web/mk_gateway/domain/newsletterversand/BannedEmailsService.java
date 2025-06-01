//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.ClientAuthException;
import de.egladil.web.mk_gateway.domain.error.InaccessableEndpointException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.newsletterversand.api.BannedEmailsResponseDto;
import de.egladil.web.mk_gateway.infrastructure.restclient.AuthproviderRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * BannedEmailsService
 */
@ApplicationScoped
public class BannedEmailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BannedEmailsService.class);

	@ConfigProperty(name = "mk-admin-app.client-id")
	String adminClientId;

	@ConfigProperty(name = "mk-admin-app.client-secret")
	String adminClientSecret;

	@Inject
	@RestClient
	AuthproviderRestClient authproviderRestClient;

	/**
	 * Holt vom authprovider eine Liste mit gebannten Mailadressen.
	 *
	 * @return List
	 */
	public List<String> getBannedEmails() {

		String nonce = UUID.randomUUID().toString();

		try (Response response = authproviderRestClient.getBannedEmails(adminClientId, adminClientSecret, nonce)) {

			BannedEmailsResponseDto responsePayload = response.readEntity(BannedEmailsResponseDto.class);

			if (!nonce.equals(responsePayload.getNonce())) {
				LOGGER.error("Das nonce des Aufrufs wurde geändert");
				throw new ClientAuthException();
			}

			return responsePayload.getBannedEmails() != null ? responsePayload.getBannedEmails() : new ArrayList<>();

		} catch (WebApplicationException e) {

			ResponsePayload responsePayload = e.getResponse().readEntity(ResponsePayload.class);

			MessagePayload messagePayload = responsePayload.getMessage();

			String message = "Konnte die gebannten Mailadressen nicht holen: " + messagePayload.getMessage();

			LOGGER.error(message);

			throw new MkGatewayRuntimeException(message);

		} catch (ProcessingException processingException) {

			LOGGER.error("endpoint authprovider ist nicht erreichbar");

			throw new InaccessableEndpointException("Der Endpoint authprovider ist nicht erreichbar. ");
		}
	}
}
