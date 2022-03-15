// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mustertexte.MustertexteService;
import de.egladil.web.mk_gateway.domain.mustertexte.api.MustertextAPIModel;

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

	@Context
	SecurityContext securityContext;

	@GET
	public Response loadMustertexte() {

		ResponsePayload responsePayload = mustertexteService.loadMustertexte();

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("{mustertextID}")
	public Response loadMustertextDetails(@UuidString @PathParam(value = "mustertextID") final String mustertextID) {

		ResponsePayload responsePayload = mustertexteService.loadDetails(new Identifier(mustertextID));

		return Response.ok(responsePayload).build();

	}

	@POST
	public Response insertMustertext(final MustertextAPIModel mustertextApiModel) {

		String adminUuid = securityContext.getUserPrincipal().getName();

		ResponsePayload responsePayload = mustertexteService.mustertextSpeichern(mustertextApiModel, adminUuid);

		return Response.ok(responsePayload).build();
	}

	@PUT
	@Path("{mustertextID}")
	public Response updateMustertext(@UuidString @PathParam(
		value = "mustertextID") final String mustertextID, final MustertextAPIModel mustertextApiModel) {

		String adminUuid = securityContext.getUserPrincipal().getName();

		ResponsePayload responsePayload = mustertexteService.mustertextSpeichern(mustertextApiModel, adminUuid);

		return Response.ok(responsePayload).build();
	}

	@DELETE
	@Path("{mustertextID}")
	public Response deleteMustertext(@UuidString @PathParam(value = "mustertextID") final String mustertextID) {

		ResponsePayload responsePayload = mustertexteService.loadDetails(new Identifier(mustertextID));

		return Response.ok(responsePayload).build();
	}
}
