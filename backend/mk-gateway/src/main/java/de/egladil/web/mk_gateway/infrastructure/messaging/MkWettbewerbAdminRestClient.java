// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.apimodel.WettbewerbAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.WettbewerbID;

/**
 * MkWettbewerbAdminRestClient
 */
@RegisterRestClient
@Path("/mk-wettbewerb-admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MkWettbewerbAdminRestClient {

	@GET
	@Path("/wettbewerbe")
	Response loadWettbewerbe(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String principalName);

	@GET
	@Path("/wettbewerbe/wettbewerb/{jahr}")
	Response getWettbewerbMitJahr(@PathParam(value = "jahr") final Integer jahr, @HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String principalName);

	@POST
	@Path("/wettbewerbe/wettbewerb")
	Response createWettbewerb(WettbewerbAPIModel data, @HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String principalName);

	@PUT
	@Path("/wettbewerbe/wettbewerb")
	Response updateWettbewerb(WettbewerbAPIModel data, @HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String principalName);

	@PUT
	@Path("wettbewerbe/wettbewerb/status")
	Response moveWettbewerbOn(WettbewerbID data, @HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String principalName);

}
