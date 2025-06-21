// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.GuiVersionService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * GuiVersionResource
 */
@RequestScoped
@Path("guiversion")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GuiVersionResource {

	@Inject
	GuiVersionService guiVersionService;

	@Inject
	DevDelayService delayService;

	@GET
	@Operation(
		operationId = "getExpectedGuiVersion",
		summary = "Gibt das zurück, was in der GUI im Footer stehen sollte. Wegen der hartnäckigen Weigerung von Browsern, die trotz nocache eine neuere Version vom Server zu laden.")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = MessagePayload.class)))
	public Response getExcpectedGuiVersion() {

		this.delayService.pause();

		String guiVersion = guiVersionService.getExcpectedGuiVersion();

		return Response.ok().entity(ResponsePayload.messageOnly(MessagePayload.info(guiVersion))).build();
	}
}
