// =====================================================
// Project: mkv-api-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.application.rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.mkv_api_gateway.application.AuthResult;

/**
 * MkvAppUserResource ist die Resource für die Lehrer und Privatmenschen der mkv-app.
 */
@RequestScoped
@Path("/mkv-app/users")
@Produces(MediaType.APPLICATION_JSON)
public class MkvAppUserResource {

	@POST
	public Response createUser(final AuthResult authResult) {

		return null;
	}

}
