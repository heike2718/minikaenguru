// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.rest;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.AktuelleTeilnahmeService;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahme;

/**
 * TeilnahmenResource
 */
@Path("/teilnahmen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TeilnahmenResource {

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	@GET
	@Path("/angemeldet/{teilnahmenummer}")
	public Response getAngemeldet(@PathParam(value = "teilnahmenummer") final String teilnahmenummer) {

		Optional<Teilnahme> optTeilnahme = aktuelleTeilnahmeService.aktuelleTeilnahme(teilnahmenummer);

		return Response.ok(new ResponsePayload(MessagePayload.ok(), Boolean.valueOf(optTeilnahme.isPresent()))).build();
	}

}
