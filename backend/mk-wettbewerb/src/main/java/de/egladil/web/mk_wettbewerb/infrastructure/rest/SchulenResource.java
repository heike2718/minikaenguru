// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb.MkvServerApp;
import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.guimodel.SchuleDashboardModel;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.SchuleDetailsService;

/**
 * SchulenResource
 */
@Path("/schulen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SchulenResource {

	@Inject
	SchuleDetailsService schuleDetailsService;

	@GET
	@Path("/details/{schulkuerzel}")
	public Response getSchuleDetails(@PathParam(value = "schulkuerzel") final String schulkuerzel, @HeaderParam(
		value = MkvServerApp.UUID_HEADER_NAME) final String principalName) {

		SchuleDashboardModel data = schuleDetailsService.ermittleSchuldetails(new Identifier(principalName),
			new Identifier(schulkuerzel));

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), data);

		return Response.ok(responsePayload).build();
	}

}
