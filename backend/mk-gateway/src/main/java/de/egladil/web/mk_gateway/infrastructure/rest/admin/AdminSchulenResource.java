// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.List;
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
import de.egladil.web.mk_gateway.domain.teilnahmen.admin.AdminSchulenService;
import de.egladil.web.mk_gateway.domain.teilnahmen.admin.SchuleAdminOverview;
import de.egladil.web.mk_gateway.domain.uploadmonitoring.api.UploadMonitoringInfo;
import de.egladil.web.mk_gateway.domain.uploadmonitoring.api.UploadMonitoringService;

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

	@GET
	@Path("{schulkuerzel}")
	public Response getSchulinfos(@PathParam(
		value = "schulkuerzel") final String schulkuerzel) {

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

		List<UploadMonitoringInfo> trefferliste = uploadMonitoringService
			.findUploadsKlassenlisteWithTeilnahmenummer(schulkuerzel);

		return Response.ok().entity(new ResponsePayload(MessagePayload.ok(), trefferliste)).build();
	}

}
