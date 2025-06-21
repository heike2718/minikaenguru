// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.infrastructure.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import de.egladil.web.minikaenguru_statistik.domain.about.AboutDto;
import de.egladil.web.minikaenguru_statistik.domain.about.AboutService;
import de.egladil.web.minikaenguru_statistik.domain.dto.MessagePayload;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * AboutResource
 */
@Path("api/about")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "About", description = "Stellt Informationen über diese API zur Verfügung")
public class AboutResource {

	@Inject
	AboutService aboutService;

	@Path("")
	@GET
	@Operation(
		operationId = "getInfos",
		summary = "Gibt Infos über die API zurück.")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = AboutDto.class)))
	@APIResponse(
		name = "ServerError",
		description = "Serverfehler",
		responseCode = "500",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	public Response getInfos() {

		AboutDto responsePayload = aboutService.getAboutInfo();

		return Response.ok(responsePayload).build();

	}

}
