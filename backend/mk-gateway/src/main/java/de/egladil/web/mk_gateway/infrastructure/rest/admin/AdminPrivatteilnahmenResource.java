// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.teilnahmen.admin.AdminPrivatteilnahmenService;
import de.egladil.web.mk_gateway.domain.teilnahmen.admin.PrivatteilnahmeAdminOverview;

/**
 * AdminPrivatteilnahmenResource
 */
@RequestScoped
@Path("admin/privatteilnahmen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminPrivatteilnahmenResource {

	@Inject
	AdminPrivatteilnahmenService privatteilnahmenService;

	@Context
	SecurityContext securityContext;

	@GET
	@Path("{teilnahmenummer}")
	public Response getTeilnahmeInfos(@PathParam(
		value = "teilnahmenummer") final String teilnahmenummer) {

		String userUuid = securityContext.getUserPrincipal().getName();

		Optional<PrivatteilnahmeAdminOverview> optOverview = privatteilnahmenService
			.ermittlePrivatteilnahmeMitDetails(teilnahmenummer, userUuid);

		if (optOverview.isEmpty()) {

			throw new NotFoundException();
		}

		return Response.ok(new ResponsePayload(MessagePayload.ok(), optOverview.get())).build();
	}

}
