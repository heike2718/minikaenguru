// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelService;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;

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
	LoesungszettelService loesungszettelService;

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@GET
	@Path("{loesungszettelID}")
	public Response getLoesungszettelWithID(@PathParam(
		value = "loesungszettelID") @NotBlank @UuidString final String loesungszettelID) {

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		LoesungszettelAPIModel result = loesungszettelService.findLoesungszettelWithID(new Identifier(loesungszettelID),
			veranstalterID);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), result);

		return Response.ok(responsePayload).build();
	}

	@POST
	public Response addLoesungszettel(final LoesungszettelAPIModel loesungszetteldaten) {

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		ResponsePayload responsePayload = loesungszettelService.loesungszettelAnlegen(loesungszetteldaten, veranstalterID);

		return Response.ok(responsePayload).build();
	}

	@PUT
	public Response changeLoesungszettel(final LoesungszettelAPIModel loesungszetteldaten) {

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		LoesungszettelpunkteAPIModel result = loesungszettelService.loesungszettelAendern(loesungszetteldaten, veranstalterID);

		String msg = MessageFormat.format(applicationMessages.getString("loesungszettel.addOrChange.success"),
			new Object[] { result.punkte(), Integer.valueOf(result.laengeKaengurusprung()) });

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), result);

		return Response.ok(responsePayload).build();
	}

	@DELETE
	@Path("{loesungszettelID}")
	public Response deleteLoesungszettelWithID(@PathParam(
		value = "loesungszettelID") @NotBlank @UuidString final String loesungszettelID) {

		Identifier veranstalterID = new Identifier(securityContext.getUserPrincipal().getName());

		loesungszettelService.loesungszettelLoeschenWithAuthorizationCheck(new Identifier(loesungszettelID),
			veranstalterID);

		String message = applicationMessages.getString("loesungszettel.delete.success");

		ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload.info(message));

		return Response.ok(responsePayload).build();
	}

}
