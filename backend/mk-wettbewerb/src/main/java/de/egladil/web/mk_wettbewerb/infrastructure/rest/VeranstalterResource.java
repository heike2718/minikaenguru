// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb.MkvServerApp;
import de.egladil.web.mk_wettbewerb.domain.apimodel.PrivatveranstalterAPIModel;
import de.egladil.web.mk_wettbewerb.domain.personen.CreateOrUpdateLehrerCommand;
import de.egladil.web.mk_wettbewerb.domain.personen.CreateOrUpdatePrivatpersonCommand;
import de.egladil.web.mk_wettbewerb.domain.personen.LehrerService;
import de.egladil.web.mk_wettbewerb.domain.personen.PrivatpersonService;
import de.egladil.web.mk_wettbewerb.domain.personen.ZugangUnterlagenService;

/**
 * VeranstalterResource
 */
@Path("/veranstalter")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VeranstalterResource {

	@Inject
	LehrerService lehrerService;

	@Inject
	PrivatpersonService privatpersonService;

	@Inject
	ZugangUnterlagenService zugangUnterlagenService;

	@POST
	@Path("/lehrer")
	public Response createLehrer(final CreateOrUpdateLehrerCommand data) {

		this.lehrerService.addLehrer(data);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("Veranstalter (LEHRER) wurde angelegt"))).build();
	}

	@POST
	@Path("/privat")
	public Response createPrivatperson(final CreateOrUpdatePrivatpersonCommand data) {

		this.privatpersonService.addPrivatperson(data);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("Veranstalter (PRIVAT) wurde angelegt"))).build();
	}

	@PUT
	@Path("/lehrer")
	public Response updateLehrer(final CreateOrUpdateLehrerCommand lehrerData) {

		return Response.serverError().entity(ResponsePayload.messageOnly(MessagePayload.error("noch nicht implementiert"))).build();
	}

	@PUT
	@Path("/privat")
	public Response updatePrivatperson(final CreateOrUpdatePrivatpersonCommand lehrerData) {

		return Response.serverError().entity(ResponsePayload.messageOnly(MessagePayload.error("noch nicht implementiert"))).build();
	}

	@GET
	@Path("/privat")
	public Response getPrivatveranstalter(@HeaderParam(
		value = MkvServerApp.UUID_HEADER_NAME) final String principalName) {

		PrivatveranstalterAPIModel privatveranstalter = privatpersonService.findPrivatperson(principalName);
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), privatveranstalter);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("/unterlagen/zugangsstatus")
	public Response hatZugangZuUnterlagen(@HeaderParam(
		value = MkvServerApp.UUID_HEADER_NAME) final String principalName) {

		boolean hat = zugangUnterlagenService.hatZugang(principalName);

		return Response.ok(new ResponsePayload(MessagePayload.ok(), Boolean.valueOf(hat))).build();
	}
}
