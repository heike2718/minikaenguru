// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.GuiVersionService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * AdminGuiVersionResource
 */
@RequestScoped
@Path("admin/guiversion")
@Produces(MediaType.APPLICATION_JSON)
public class AdminGuiVersionResource {

	@Inject
	GuiVersionService guiVersionService;

	@Inject
	DevDelayService delayService;

	@GET
	public Response getExcpectedGuiVersion() {

		this.delayService.pause();

		String guiVersion = guiVersionService.getExcpectedGuiVersion();

		return Response.ok().entity(ResponsePayload.messageOnly(MessagePayload.info(guiVersion))).build();
	}
}
