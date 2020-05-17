// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.teilnahmen;

import java.security.Principal;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.mk_gateway.domain.wettbewerb.VeranstalterService;

/**
 * TeilnahmenResource
 */
@RequestScoped
@Path("/wettbewerb/teilnahmen")
@Produces(MediaType.APPLICATION_JSON)
public class TeilnahmenResource {

	@Inject
	VeranstalterService veranstalterService;

	@Context
	SecurityContext securityContext;

	@GET
	@Path("/teilnahmenummern")
	public Response getOwnTeilnahmenummern() {

		Principal principal = securityContext.getUserPrincipal();

		return veranstalterService.getTeilnahmenummern(principal.getName());
	}
}
