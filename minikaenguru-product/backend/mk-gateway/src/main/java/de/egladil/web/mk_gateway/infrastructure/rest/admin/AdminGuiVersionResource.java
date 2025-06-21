// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.GuiVersionService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
	@Operation(
		operationId = "getExcpectedGuiVersion",
		summary = "Gibt die erwartete GUI-Version zurück.")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = MessagePayload.class)))
	public Response getExcpectedGuiVersion() {

		this.delayService.pause();

		String guiVersion = guiVersionService.getExcpectedGuiVersion();

		return Response.ok().entity(ResponsePayload.messageOnly(MessagePayload.info(guiVersion))).build();
	}
}
