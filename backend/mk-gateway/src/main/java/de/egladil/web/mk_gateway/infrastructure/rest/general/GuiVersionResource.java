// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.GuiVersionService;

/**
 * GuiVersionResource
 */
@RequestScoped
@Path("guiversion")
@Produces(MediaType.APPLICATION_JSON)
public class GuiVersionResource {

	@Inject
	GuiVersionService guiVersionService;

	@GET
	public Response getExcpectedGuiVersion() {

		String guiVersion = guiVersionService.getExcpectedGuiVersion();

		return Response.ok().entity(ResponsePayload.messageOnly(MessagePayload.info(guiVersion))).build();
	}
}
