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

import de.egladil.web.mkbiza_api.domain.ConstraintViolationResponse;
import de.egladil.web.mkbiza_api.domain.Klassenstufe;
import de.egladil.web.mkbiza_api.domain.aufgaben.AufgabenService;
import de.egladil.web.mkbiza_api.domain.aufgaben.MinikaenguruKlassenstufeDto;
import de.egladil.web.mkbiza_api.domain.aufgaben.StatistikAufgabe;
import de.egladil.web.mkbiza_api.domain.dto.MessagePayload;
import de.egladil.web.mkbiza_api.domain.validation.MkbizaRegexps;
import de.egladil.web.mkbiza_api.domain.wettbewerbe.Wettbewerb;
import de.egladil.web.mkbiza_api.domain.wettbewerbe.WettbewerbDetails;
import de.egladil.web.mkbiza_api.domain.wettbewerbe.WettbewerbService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
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

	@Inject
	AufgabenService aufgabenService;

	@GET
	@Operation(
		operationId = "getWettbewerbe",
		summary = "Gibt Minikänguru-Wettbewerbe sowie deren Status und Mediane zurück")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = Wettbewerb.class)))
	@APIResponse(
		name = "Not Found",
		description = "NotFound",
		responseCode = "404",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	@APIResponse(
		name = "ServerError",
		description = "Serverfehler - Details stehen im server.log",
		responseCode = "500",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	public Response getWettbewerbe() {

		List<Wettbewerb> wettbewerbe = wettbewerbService.loadWettbewerbe();

		return Response.ok(wettbewerbe).build();

	}

	@GET
	@Path("{jahr}")
	@Operation(
		operationId = "getStatistikWettbewerb",
		summary = "Gibt die Gesamtstatistik eines Wettbewerbs zurück")
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
		content = @Content(schema = @Schema(implementation = ConstraintViolationResponse.class)))
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
	// @formatter:off
	public Response getStatistikWettbewerb(
		@Pattern(regexp = MkbizaRegexps.VALID_JAHR, message = MkbizaRegexps.MSG_INVALID_JAHR) @PathParam(value = "jahr") final String jahr) {
	// @formatter:on

		try {

			Integer wettbewerbsjahr = Integer.valueOf(jahr);
			WettbewerbDetails responsePayload = wettbewerbService.getWettbewerbDetails(wettbewerbsjahr);

			return Response.ok(responsePayload).build();
		} catch (NumberFormatException e) {

			return Response.status(400).entity(MessagePayload.error("jahr ist nicht numerisch")).build();
		}
	}

	@Path("{jahr}/{klassenstufe}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	@Operation(
		operationId = "getAufgabenMinikaenguruwettbewerb",
		summary = "Gibt die Aufgaben eines bestimmten Minikänguru-Wettbewerbs zurück.",
		description = "Nur freigegebene Wettbewerbe werden geliefert.")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "jahr",
			description = "Jahr des Wettbewerbs",
			required = true),
		@Parameter(
			in = ParameterIn.PATH,
			name = "klassenstufe",
			description = "Eins von IKID,EINS,ZWEI - die Klassenstufe.",
			required = true) })
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = MinikaenguruKlassenstufeDto.class)))
	@APIResponse(
		name = "BadRequest",
		description = "Input-Validierung ging schief.",
		responseCode = "400")
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
		@Pattern(regexp = MkbizaRegexps.VALID_JAHR, message = MkbizaRegexps.MSG_INVALID_JAHR) @PathParam(value = "jahr") final String jahr,
		@PathParam(value = "klassenstufe") final Klassenstufe klassenstufe) {
	// @formatter:on

		MinikaenguruKlassenstufeDto aufgaben = wettbewerbService.getAufgabenWettbewerb(jahr, klassenstufe);

		return Response.ok(aufgaben).build();
	}

	// TODO GET statistik/{jahr}/{klassenstufe} soll die Prozentränge zurückgeben.

	@GET
	@Path("{jahr}/{klassenstufe}/aufgaben/{nummer}")
	@Operation(
		operationId = "getStatistikAufgabe",
		summary = "Gibt die Statistik für eine spezielle Aufgabe zurück")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "jahr",
			description = "Jahr des Wettbewerbs",
			required = true),
		@Parameter(
			in = ParameterIn.PATH,
			name = "klassenstufe",
			description = "Klassenstufe",
			required = true),
		@Parameter(
			in = ParameterIn.PATH,
			name = "nummer",
			description = "Nummer der Aufgabe",
			required = true) })
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = StatistikAufgabe.class)))
	@APIResponse(
		name = "BadRequest",
		description = "Inputvalidierung schlug fehl",
		responseCode = "400",
		content = @Content(schema = @Schema(implementation = ConstraintViolationResponse.class)))
	@APIResponse(
		name = "Unauthorized",
		description = "S2S-Autentifizierung schlug fehl",
		responseCode = "401",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	@APIResponse(
		name = "NotFound",
		description = "Jahr existsiert nicht oder Wettbewerb ist noch nicht beendet oder Aufgabennummer existsiert nicht",
		responseCode = "404",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	@APIResponse(
		name = "ServerError",
		description = "Serverfehler",
		responseCode = "500",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	// @formatter:off
	public Response getStatistikAufgabe(
		@Pattern(regexp = MkbizaRegexps.VALID_JAHR, message = MkbizaRegexps.MSG_INVALID_JAHR) @PathParam(value = "jahr") final String jahr,
		@NotNull @PathParam(value = "klassenstufe") final Klassenstufe klassenstufe,
		@Pattern(regexp = MkbizaRegexps.VALID_AUFGABENNUMMER, message = MkbizaRegexps.MSG_INVALID_AUFGABENNUMMER) @PathParam(value = "nummer") final String aufgabennummer) {
	// @formatter:on

		try {

			Integer wettbewerbsjahr = Integer.valueOf(jahr);

			StatistikAufgabe responsePayload = aufgabenService.getStatistikZuAufgabe(wettbewerbsjahr, klassenstufe,
				aufgabennummer);

			return Response.ok(responsePayload).build();
		} catch (NumberFormatException e) {

			return Response.status(400).entity(MessagePayload.error("jahr ist nicht numerisch")).build();
		}
	}
}
