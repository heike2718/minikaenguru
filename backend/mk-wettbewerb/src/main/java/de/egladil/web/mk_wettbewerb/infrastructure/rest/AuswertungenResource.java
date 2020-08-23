// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.mk_wettbewerb.domain.auswertungen.ImportLoesungszettelService;

/**
 * AuswertungenResource
 */
@Path("/auswertungen")
@Produces(MediaType.APPLICATION_JSON)
public class AuswertungenResource {

	@Inject
	ImportLoesungszettelService importLoesungszettelService;

	@GET
	@Path("/import/loesungszettel")
	@Deprecated(forRemoval = true)
	public Response triggerImportLoesungszettel() {

		importLoesungszettelService.run();

		return Response.ok().build();

	}

}
