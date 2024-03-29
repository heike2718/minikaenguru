// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * AdminLoesungszettelResource
 */
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Path("admin/loesungszettel")
@Produces(MediaType.APPLICATION_JSON)
public class AdminLoesungszettelResource {

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("{jahr}/size")
	public Response getLoesungszettelCount(@PathParam(value = "jahr") final Integer jahr) {

		this.delayService.pause();

		long result = loesungszettelRepository.anzahlForWettbewerb(new WettbewerbID(Integer.valueOf(jahr)));

		return Response.ok().entity(new ResponsePayload(MessagePayload.ok(), result)).build();
	}

	@GET
	@Path("{jahr}")
	public Response getLoesungszettel(@PathParam(value = "jahr") final Integer jahr, @QueryParam(
		value = "limit") final int limit, @QueryParam(
			value = "offset") final int offset) {

		this.delayService.pause();

		WettbewerbID wettbewerbId = new WettbewerbID(jahr);
		List<LoesungszettelAPIModel> result = loesungszettelRepository.loadLoadPageForWettbewerb(wettbewerbId, limit, offset)
			.stream().map(lz -> LoesungszettelAPIModel.createFromLoesungszettel(lz)).collect(Collectors.toList());
		return Response.ok().entity(new ResponsePayload(MessagePayload.ok(), result)).build();
	}

}
