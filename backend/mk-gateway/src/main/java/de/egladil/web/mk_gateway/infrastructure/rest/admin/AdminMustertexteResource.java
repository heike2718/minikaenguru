// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.mustertexte.MustertexteService;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertextkategorie;

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

	@GET
	public Response loadMustertexte(@QueryParam(value = "kategorie") final String kategorie) {

		try {

			Mustertextkategorie mustertextkategorie = Mustertextkategorie.valueOf(kategorie);
			ResponsePayload responsePayload = mustertexteService.getMustertexteByKategorie(mustertextkategorie);

			return Response.ok(responsePayload).build();
		} catch (IllegalArgumentException e) {

			return Response.status(400).entity(ResponsePayload.messageOnly(MessagePayload.error("ungültige Kategorie"))).build();
		}
	}

}
