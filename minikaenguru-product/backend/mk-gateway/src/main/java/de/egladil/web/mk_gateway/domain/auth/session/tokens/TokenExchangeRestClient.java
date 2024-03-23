// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.session.tokens;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.commons_validation.payload.OAuthClientCredentials;

/**
 * TokenExchangeRestClient für authprovider
 */
@RegisterRestClient
@Path("token")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TokenExchangeRestClient {

	@PUT
	@Path("exchange/{oneTimeToken}")
	public Response exchangeOneTimeTokenWithJwt(@PathParam(
		value = "oneTimeToken") @UuidString final String oneTimeToken, final OAuthClientCredentials clientCredentials);

}
