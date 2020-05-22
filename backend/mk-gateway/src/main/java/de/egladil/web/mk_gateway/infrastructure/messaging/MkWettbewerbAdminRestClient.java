// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.mk_gateway.MkGatewayApp;

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

}
