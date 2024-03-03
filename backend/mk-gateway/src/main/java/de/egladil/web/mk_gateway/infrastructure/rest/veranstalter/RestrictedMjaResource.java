// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.mk_gateway.domain.feedback.scores.AufgabenVorschauService;
import de.egladil.web.mk_gateway.domain.feedback.scores.dto.AufgabenvorschauDto;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * RestrictedMjaResource
 */
@Path("mja-api")
public class RestrictedMjaResource {

	@Inject
	AufgabenVorschauService aufgabenVorschauService;

	@Path("aufgaben/{klasse}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	@Operation(
		operationId = "Läd die Aufgaben der gegebenen Klassenstufe es aktuellen Wettbewerbs vom mja-api")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "klasse",
			description = "die Klassenstufe.",
			required = true) })
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = AufgabenvorschauDto.class)))
	@APIResponse(
		name = "NotFound",
		description = "Gibt es nicht",
		responseCode = "404")
	@APIResponse(
		name = "ServerError",
		description = "Serverfehler",
		responseCode = "500",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	// @formatter:off
	public Response getAufgabenMinikaenguruwettbewerb(
		@PathParam(value = "klasse") final Klassenstufe klassenstufe) {
	// @formatter:on

		AufgabenvorschauDto aufgaben = aufgabenVorschauService.getAufgabenvorschauAktuellerWettbewerb(klassenstufe);
		return Response.ok(aufgaben).build();
	}
}
