// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * AdminTeilnahmenResource
 */
@RequestScoped
@Path("/admin/teilnahmen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminTeilnahmenResource {

	@GET
	@Path("{teilnahmenummer}")
	public Response getTeilnahmenuebersicht(@PathParam(value = "teilnahmenummer") final String teilnahmenummer) {

		return Response
			.ok(ResponsePayload
				.messageOnly(MessagePayload.warn("GET /admin/teilnahmen/teilnahmenummet ist noch nicht implementiert")))
			.build();
	}

}
