// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import java.util.List;
import java.util.Optional;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbListAPIModel;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * PublicWettbewerbResource
 */
@RequestScoped
@Path("wettbewerbe")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PublicWettbewerbResource {

	@Inject
	WettbewerbService wettewerbService;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("aktueller")
	@Blocking
	public Response getAktuellenWettbewerb() {

		this.delayService.pause();

		Optional<Wettbewerb> optWettbewerb = this.wettewerbService.aktuellerWettbewerb();

		if (!optWettbewerb.isPresent()) {

			throw new NotFoundException();
		}

		WettbewerbAPIModel wettbewerbAPIModel = WettbewerbAPIModel.fromWettbewerb(optWettbewerb.get());

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), wettbewerbAPIModel);

		return Response.ok(responsePayload).build();

	}

	@GET
	public Response loadWettbewerbe() {

		this.delayService.pause();

		List<WettbewerbListAPIModel> wettbewerbe = this.wettewerbService.alleWettbewerbeHolen();

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), wettbewerbe);

		return Response.ok(responsePayload).build();
	}
}
