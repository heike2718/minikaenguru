// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.teilnahmen.admin.AdminSchulenService;
import de.egladil.web.mk_gateway.domain.teilnahmen.admin.SchuleAdminOverview;
import de.egladil.web.mk_gateway.domain.uploadmonitoring.api.UploadMonitoringInfo;
import de.egladil.web.mk_gateway.domain.uploadmonitoring.api.UploadMonitoringService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * AdminSchulenResource
 */
@RequestScoped
@Path("admin/schulen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminSchulenResource {

	@Context
	SecurityContext securityContext;

	@Inject
	AdminSchulenService adminSchulenService;

	@Inject
	UploadMonitoringService uploadMonitoringService;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("{schulkuerzel}")
	public Response getSchulinfos(@PathParam(
		value = "schulkuerzel") final String schulkuerzel) {

		this.delayService.pause();

		Optional<SchuleAdminOverview> optSchule = adminSchulenService.ermittleSchuleMitDetails(schulkuerzel,
			securityContext.getUserPrincipal().getName());

		if (optSchule.isEmpty()) {

			throw new NotFoundException();
		}

		return Response.ok(new ResponsePayload(MessagePayload.ok(), optSchule.get())).build();
	}

	@GET
	@Path("{schulkuerzel}/uploads/klassenlisten")
	public Response getUploadsKlassenlisten(@PathParam(
		value = "schulkuerzel") final String schulkuerzel) {

		this.delayService.pause();

		List<UploadMonitoringInfo> trefferliste = uploadMonitoringService
			.findUploadsKlassenlisteWithTeilnahmenummer(schulkuerzel);

		return Response.ok().entity(new ResponsePayload(MessagePayload.ok(), trefferliste)).build();
	}

}
