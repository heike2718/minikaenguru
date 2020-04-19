// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.application.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb.application.commands.CreateOrUpdateLehrerCommand;
import de.egladil.web.mk_wettbewerb.application.commands.CreateOrUpdatePrivatpersonCommand;
import de.egladil.web.mk_wettbewerb.domain.model.personen.AddLehrerService;
import de.egladil.web.mk_wettbewerb.domain.model.personen.AddPrivatpersonService;

/**
 * PersonenResource
 */
@RequestScoped
@Path("/personen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonenResource {

	@Inject
	AddLehrerService addLehrerService;

	@Inject
	AddPrivatpersonService addPrivatpersonService;

	@POST
	@Path("/lehrer")
	public Response createLehrer(final CreateOrUpdateLehrerCommand data) {

		this.addLehrerService.addLehrer(data);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("Veranstalter (LEHRER) wurde angelegt"))).build();
	}

	@POST
	@Path("/privat")
	public Response createPrivatperson(final CreateOrUpdatePrivatpersonCommand data) {

		this.addPrivatpersonService.addPrivatperson(data);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("Veranstalter (PRIVAT) wurde angelegt"))).build();
	}

	@PUT
	@Path("/lehrer")
	public Response updateLehrer(final CreateOrUpdateLehrerCommand lehrerData) {

		return null;
	}

	@PUT
	@Path("/privat")
	public Response updatePrivatperson(final CreateOrUpdatePrivatpersonCommand lehrerData) {

		return null;
	}

}
