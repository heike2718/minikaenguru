// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.infrastructure.resources;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import de.egladil.web.mkbiza_api.domain.dto.MessagePayload;
import de.egladil.web.mkbiza_api.domain.wettbewerbe.Wettbewerb;
import de.egladil.web.mkbiza_api.domain.wettbewerbe.WettbewerbDetails;
import de.egladil.web.mkbiza_api.domain.wettbewerbe.WettbewerbService;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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
			schema = @Schema(type = SchemaType.ARRAY, implementation = Wettbewerb.class)))
	@APIResponse(
		name = "ServerError",
		description = "Serverfehler - Details stehen im server.log",
		responseCode = "500",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	public Response getWettbewerbsjahre() {

		List<Wettbewerb> wettbewerbe = wettbewerbService.loadWettbewerbe();

		return Response.ok(wettbewerbe).build();

	}

	@GET
	@Path("{jahr}")
	@Operation(
		operationId = "getStatistikWettbewerb",
		summary = "Gibt die Statistik eines Wettbewerbs zurück")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "jahr",
			description = "Jahr des Wettbewerbs - 4stellige Jahreszahl",
			required = true) })
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = WettbewerbDetails.class)))
	@APIResponse(
		name = "BadRequest",
		description = "Inputvalidierung schlug fehl",
		responseCode = "400",
		content = @Content(schema = @Schema(implementation = ConstraintViolation.class)))
	@APIResponse(
		name = "NotFound",
		description = "Wettbewerb existsiert nicht oder ist noch nicht beendet",
		responseCode = "404",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	@APIResponse(
		name = "ServerError",
		description = "Serverfehler - Details stehen im server.log",
		responseCode = "500",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	public Response getStatistikWettbewerb(@Pattern(
		regexp = "^[\\d]{4}$", message = "jahr ist nicht numerisch oder hat nicht die richtige Länge") @PathParam(
			value = "jahr") final String jahr) {

		WettbewerbDetails responsePayload = wettbewerbService.getWettbewerbDetails(jahr);

		return Response.ok(responsePayload).build();
	}

}
