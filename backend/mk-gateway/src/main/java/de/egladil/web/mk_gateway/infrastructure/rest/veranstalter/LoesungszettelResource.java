// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.OnlineLoesungszettelService;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * LoesungszettelResource
 */
@RequestScoped
@Path("loesungszettel")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoesungszettelResource {

	@Context
	SecurityContext securityContext;

	@Inject
	OnlineLoesungszettelService loesungszettelService;

	@Inject
	DevDelayService delayService;

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@GET
	@Path("{loesungszettelID}")
	public Response getLoesungszettelWithID(@PathParam(
		value = "loesungszettelID") @NotBlank @UuidString final String loesungszettelID) {

		this.delayService.pause();

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		LoesungszettelAPIModel result = loesungszettelService.findLoesungszettelWithID(new Identifier(loesungszettelID),
			veranstalterID);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), result);

		return Response.ok(responsePayload).build();
	}

	@POST
	public Response addLoesungszettel(final LoesungszettelAPIModel loesungszetteldaten) {

		this.delayService.pause();

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		ResponsePayload responsePayload = loesungszettelService.loesungszettelAnlegen(loesungszetteldaten, veranstalterID);

		return Response.ok(responsePayload).build();
	}

	@PUT
	public Response changeLoesungszettel(final LoesungszettelAPIModel loesungszetteldaten) {

		this.delayService.pause();

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		ResponsePayload responsePayload = loesungszettelService.loesungszettelAendern(loesungszetteldaten, veranstalterID);

		return Response.ok(responsePayload).build();
	}

	@DELETE
	@Path("{loesungszettelID}")
	public Response deleteLoesungszettelWithID(@PathParam(
		value = "loesungszettelID") @NotBlank @UuidString final String loesungszettelID) {

		this.delayService.pause();

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		loesungszettelService.loesungszettelLoeschenWithAuthorizationCheck(new Identifier(loesungszettelID),
			veranstalterID);

		String message = applicationMessages.getString("loesungszettel.delete.success");

		ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload.info(message));

		return Response.ok(responsePayload).build();
	}

}
