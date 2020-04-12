// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.duplicated.clientauth;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.commons_validation.payload.OAuthClientCredentials;

/**
 * OrderMkvAccessTokenRestClient nach Fix von https://github.com/quarkusio/quarkus/issues/5015 ersetzen durch
 * OrderAccessTokenRestClient in mk-commons
 */
@Deprecated(forRemoval = true)
@RegisterRestClient
@Path("clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface OrderMkvAccessTokenRestClient {

	@POST
	@Path("/client/accesstoken")
	Response authenticateClient(OAuthClientCredentials clientCredentials);

}
