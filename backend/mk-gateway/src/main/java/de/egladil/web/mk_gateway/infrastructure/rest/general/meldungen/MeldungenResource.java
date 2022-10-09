// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general.meldungen;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.meldungen.Meldung;
import de.egladil.web.mk_gateway.domain.meldungen.MeldungenService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * MeldungenResource
 */
@Path("meldungen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MeldungenResource {

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

}
