// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.infrastructure.restclient;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.minikaenguru_statistik.domain.Klassenstufe;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * MkGatewayRestClient
 */
@RegisterRestClient(configKey = "mkgateway")
@Path("mkbiza")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MkGatewayRestClient {

	@GET
	@Path("wettbewerbe")
	// @formatter:off
	public Response loadWettbewerbsjahre(
		@HeaderParam(value = "X-CLIENT-ID") final String clientId,
		@HeaderParam(value = "Authorization") final String authHeader);
	// @formatter:on

	@GET
	@Path("wettbewerbe/{jahr}")
	// @formatter:off
	public Response getStatistikWettbewerb(
		@PathParam(value = "jahr") final Integer wettbewerbsjah,
		@HeaderParam(value = "X-CLIENT-ID") final String clientId,
		@HeaderParam(value = "Authorization") final String authHeader);
	// @formatter:on

	// @formatter:off
	@GET
	@Path("wettbewerbe/{jahr}/{klasse}")
	Response getStatistikKlassenstufe(
		@PathParam(value = "jahr") final String jahr,
		@PathParam(value = "klasse") final Klassenstufe klassenstufe,
		@HeaderParam(value = "X-CLIENT-ID") final String clientId,
		@HeaderParam(value = "Authorization") final String authHeader);
	// @formatter:on
}
