// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.restclient;

import java.time.temporal.ChronoUnit;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import de.egladil.web.commons_validation.payload.OAuthClientCredentials;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * AuthproviderRestClient
 */
/**
 * InitAccessTokenRestClient
 */
@RegisterRestClient(configKey = "authprovider")
@Path("api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AuthproviderRestClient {

	@POST
	@Path("clients/client/accesstoken")
	@Retry(maxRetries = 3, delay = 1000, abortOn = ClientWebApplicationException.class)
	@Timeout(value = 10, unit = ChronoUnit.SECONDS)
	Response authenticateClient(OAuthClientCredentials clientSecrets);

	@PUT
	@Path("token/exchange/{oneTimeToken}")
	@Retry(maxRetries = 3, delay = 1000, abortOn = ClientWebApplicationException.class)
	@Timeout(value = 10, unit = ChronoUnit.SECONDS)
	public Response exchangeOneTimeTokenWithJwt(@PathParam(value = "oneTimeToken")
	final String oneTimeToken, final OAuthClientCredentials clientCredentials);

	/**
	 * Holt die Mailadressen der für den Mailversand gebannten Veranstalter.
	 *
	 * @param clientId String (dieser Client)
	 * @param clientSecretString String (mk-gateway-secret)
	 * @param nonce String
	 * @return Response mit BannedEmailsResponseDto als Payload
	 */
	@GET
	@Path("users/banned-emails")
	public Response getBannedEmails(@HeaderParam(value = "X-CLIENT-ID")
	String clientId, @HeaderParam(value = "X-CLIENT-SECRET")
	String clientSecretString, @HeaderParam(value = "X-NONCE")
	String nonce);

}
