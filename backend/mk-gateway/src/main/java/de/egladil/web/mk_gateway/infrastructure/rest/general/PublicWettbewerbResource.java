// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
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
import de.egladil.web.mk_gateway.domain.apimodel.wettbewerb.WettbewerbAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * PublicWettbewerbResource
 */
@RequestScoped
@Path("/wettbewerb")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PublicWettbewerbResource {

	@Inject
	WettbewerbService wettewerbService;

	/**
	 * @param  wettbewerbService
	 * @return
	 */
	public static PublicWettbewerbResource createForTest(final WettbewerbService wettbewerbService) {

		PublicWettbewerbResource result = new PublicWettbewerbResource();
		result.wettewerbService = wettbewerbService;
		return result;
	}

	@GET
	@Path("/aktueller")
	public Response getAktuellenWettbewerb() {

		Optional<Wettbewerb> optWettbewerb = this.wettewerbService.aktuellerWettbewerb();

		if (!optWettbewerb.isPresent()) {

			throw new NotFoundException();
		}

		WettbewerbAPIModel wettbewerbAPIModel = WettbewerbAPIModel.fromWettbewerb(optWettbewerb.get());

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), wettbewerbAPIModel);

		return Response.ok(responsePayload).build();

	}

}
