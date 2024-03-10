// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.meldungen.Meldung;
import de.egladil.web.mk_gateway.domain.meldungen.MeldungenService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * AdminMeldungenResource
 */
@Path("admin/meldungen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminMeldungenResource {

	@Inject
	MeldungenService meldungenService;

	@Inject
	DevDelayService delayService;

	@Path("aktuelle-meldung")
	@GET
	public Response getMeldung() {

		this.delayService.pause();

		Meldung meldung = meldungenService.loadMeldung();

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), meldung);
		return Response.ok(responsePayload).build();
	}

	@Path("aktuelle-meldung")
	@POST
	public Response postMeldung(final Meldung meldung) {

		this.delayService.pause();

		meldungenService.saveMeldung(meldung);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("aktuelle Meldung erfolgreich gespeichert"))).build();

	}

	@Path("aktuelle-meldung")
	@DELETE
	public Response deleteMeldung() {

		this.delayService.pause();

		meldungenService.deleteMeldung();

		return Response.ok(ResponsePayload
			.messageOnly(MessagePayload.info("aktuelle Meldung erfolgreich geloescht (sofern es sie überhaupt gab)"))).build();

	}

}
