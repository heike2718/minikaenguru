// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.restclient;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * MjaApiRestClient
 */
@RegisterRestClient
@Path("restricted")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MjaApiRestClient {

	// @formatter:off
	@GET
	@Path("minikaenguru/{jahr}/{klasse}")
	Response getAufgabenMinikaenguruwettbewerb(
		@HeaderParam(value = "X-CLIENT-ID") final String clientId,
		@HeaderParam(value = "Authorization") final String authHeader,
		@PathParam(value = "jahr") final String jahr,
		@PathParam(value = "klasse") final Klassenstufe schwierigkeitsgrad);
	// @formatter:on

}
