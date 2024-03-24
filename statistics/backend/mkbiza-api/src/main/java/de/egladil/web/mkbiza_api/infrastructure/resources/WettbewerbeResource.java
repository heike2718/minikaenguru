// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.infrastructure.resources;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import de.egladil.web.mkbiza_api.domain.dto.MessagePayload;
import de.egladil.web.mkbiza_api.domain.wettbewerbe.WettbewerbService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * WettbewerbeResource
 */
@Path("mkbiza-api/wettbewerbe")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Wettbewerbe")
public class WettbewerbeResource {

	@Inject
	WettbewerbService wettbewerbService;

	@GET
	@Operation(
		operationId = "getWettbewerbsjahre",
		summary = "Gibt die Jahre aller beendeten Minikänguru-Wettbewerbe zurück")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = Integer.class)))
	@APIResponse(
		name = "Unauthorized",
		description = "S2S-Autentifizierung schlug fehl",
		responseCode = "401",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	@APIResponse(
		name = "ServerError",
		description = "Serverfehler",
		responseCode = "500",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	// @APIResponse(
	// name = "GatewayTimeout",
	// description = "Gegenstelle war nicht in der konfogurierten Zeit erreichbar",
	// responseCode = "504",
	// content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	public Response getWettbewerbsjahre() {

		List<Integer> wettbewerbe = wettbewerbService.loadWettbewerbsjahre();

		return Response.ok(wettbewerbe).build();

	}

}
