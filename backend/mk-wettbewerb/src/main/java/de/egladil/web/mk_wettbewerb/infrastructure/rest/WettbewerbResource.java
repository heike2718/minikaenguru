// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.rest;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb.domain.apimodel.WettbewerbAPIModel;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbService;

/**
 * WettbewerbResource
 */
@Path("/wettbewerb")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WettbewerbResource {

	@Inject
	WettbewerbService wettewerbService;

	@GET
	public Response getAktuellenWettbewerb() {

		Optional<Wettbewerb> optWettbewerb = this.wettewerbService.aktuellerWettbewerb();

		if (!optWettbewerb.isPresent()) {

			throw new NotFoundException();
		}

		WettbewerbAPIModel wettbewerbApiModel = WettbewerbAPIModel.fromWettbewerb(optWettbewerb.get());
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), wettbewerbApiModel);

		return Response.ok(responsePayload).build();
	}

}
