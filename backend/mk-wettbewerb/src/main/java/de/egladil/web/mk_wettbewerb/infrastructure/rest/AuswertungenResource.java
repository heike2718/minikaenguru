// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * AuswertungenResource
 */
@Path("/auswertungen")
@Produces(MediaType.APPLICATION_JSON)
public class AuswertungenResource {

	@GET
	public Response hello() {

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("es gibt nichts zu sehen. Gehen Sie weiter"))).build();
	}

}
