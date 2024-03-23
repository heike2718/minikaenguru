// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
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
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mustertexte.MustertexteService;
import de.egladil.web.mk_gateway.domain.mustertexte.api.MustertextAPIModel;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * AdminMustertexteResource
 */
@RequestScoped
@Path("admin/mustertexte")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminMustertexteResource {

	@Inject
	MustertexteService mustertexteService;

	@Inject
	DevDelayService delayService;

	@Context
	SecurityContext securityContext;

	@GET
	public Response loadMustertexte() {

		this.delayService.pause();

		ResponsePayload responsePayload = mustertexteService.loadMustertexte();

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("{mustertextID}")
	public Response loadMustertextDetails(@UuidString @PathParam(value = "mustertextID") final String mustertextID) {

		this.delayService.pause();

		ResponsePayload responsePayload = mustertexteService.loadDetails(new Identifier(mustertextID));

		return Response.ok(responsePayload).build();

	}

	@POST
	public Response insertMustertext(final MustertextAPIModel mustertextApiModel) {

		this.delayService.pause();

		String adminUuid = securityContext.getUserPrincipal().getName();

		ResponsePayload responsePayload = mustertexteService.mustertextSpeichern(mustertextApiModel, adminUuid);

		return Response.ok(responsePayload).build();
	}

	@PUT
	@Path("{mustertextID}")
	public Response updateMustertext(@UuidString @PathParam(
		value = "mustertextID") final String mustertextID, final MustertextAPIModel mustertextApiModel) {

		this.delayService.pause();

		String adminUuid = securityContext.getUserPrincipal().getName();

		ResponsePayload responsePayload = mustertexteService.mustertextSpeichern(mustertextApiModel, adminUuid);

		return Response.ok(responsePayload).build();
	}

	@DELETE
	@Path("{mustertextID}")
	public Response deleteMustertext(@UuidString @PathParam(value = "mustertextID") final String mustertextID) {

		this.delayService.pause();

		String adminUuid = securityContext.getUserPrincipal().getName();

		ResponsePayload responsePayload = mustertexteService.mustertextLoeschen(new Identifier(mustertextID), adminUuid);

		return Response.ok(responsePayload).build();
	}
}
