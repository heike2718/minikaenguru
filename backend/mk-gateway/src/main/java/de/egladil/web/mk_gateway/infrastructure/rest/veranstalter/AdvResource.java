// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.adv.AdvService;

/**
 * AdvResource
 */
@RequestScoped
@Path("/adv")
@Consumes(MediaType.APPLICATION_JSON)
public class AdvResource {

	@Context
	SecurityContext securityContext;

	@Inject
	AdvService advService;

	@GET
	@Path("/{schulkuerzel}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public Response downloadVertragAuftragsdatenverarbeitung(@PathParam(value = "schulkuerzel") final String schulkuerzel) {

		final DownloadData data = advService.getVertragAuftragsdatenverarbeitung(schulkuerzel,
			securityContext.getUserPrincipal().getName());

		String contentDisposition = "attachement; filename=" + data.filename();

		return Response.ok(data.data()).header("Content-Type", "application/octet-stream")
			.header("Content-Disposition", contentDisposition).build();

	}

}
