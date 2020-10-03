// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.veranstalter.admin.VeranstalterSucheService;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterUserAPIModel;

/**
 * AdminVeranstalterResource
 */
@RequestScoped
@Path("/admin/veranstalter")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminVeranstalterResource {

	@Inject
	VeranstalterSucheService sucheService;

	@POST
	@Path("suche")
	public Response findVeranstalter(final VeranstalterSuchanfrage suchanfrage) {

		List<VeranstalterUserAPIModel> treffer = sucheService.findVeranstalter(suchanfrage);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), treffer);
		return Response.ok(responsePayload).build();
	}

}
