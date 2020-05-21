// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.egladil.web.mk_wettbewerb.domain.teilnahmen.AktuelleTeilnahmeService;

/**
 * TeilnahmenResource
 */
@Path("/teilnahmen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TeilnahmenResource {

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

}
