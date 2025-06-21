// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.Optional;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.teilnahmen.admin.AdminPrivatteilnahmenService;
import de.egladil.web.mk_gateway.domain.teilnahmen.admin.PrivatteilnahmeAdminOverview;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

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

	@Inject
	DevDelayService delayService;

	@Context
	SecurityContext securityContext;

	@GET
	@Path("{teilnahmenummer}")
	public Response getTeilnahmeInfos(@PathParam(
		value = "teilnahmenummer") final String teilnahmenummer) {

		this.delayService.pause();

		String userUuid = securityContext.getUserPrincipal().getName();

		Optional<PrivatteilnahmeAdminOverview> optOverview = privatteilnahmenService
			.ermittlePrivatteilnahmeMitDetails(teilnahmenummer, userUuid);

		PrivatteilnahmeAdminOverview payload = optOverview.get();

		if (payload.anzahlTeilnahmen() == 0) {

			return Response.ok(new ResponsePayload(MessagePayload.warn("Veranstalter hat noch keine Teilnahmen"), payload)).build();
		}

		return Response.ok(new ResponsePayload(MessagePayload.ok(), payload)).build();
	}

}
