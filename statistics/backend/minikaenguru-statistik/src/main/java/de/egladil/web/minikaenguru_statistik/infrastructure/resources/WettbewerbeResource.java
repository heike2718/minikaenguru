// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.infrastructure.resources;

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

import de.egladil.web.minikaenguru_statistik.domain.ConstraintViolationResponse;
import de.egladil.web.minikaenguru_statistik.domain.Klassenstufe;
import de.egladil.web.minikaenguru_statistik.domain.dto.MessagePayload;
import de.egladil.web.minikaenguru_statistik.domain.klassenstufen.KlassenstufeDetails;
import de.egladil.web.minikaenguru_statistik.domain.klassenstufen.KlassenstufeService;
import de.egladil.web.minikaenguru_statistik.domain.validation.MkbizaRegexps;
import de.egladil.web.minikaenguru_statistik.domain.wettbewerbe.Wettbewerb;
import de.egladil.web.minikaenguru_statistik.domain.wettbewerbe.WettbewerbDetails;
import de.egladil.web.minikaenguru_statistik.domain.wettbewerbe.WettbewerbService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
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
@Path("api/wettbewerbe")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Wettbewerbe")
public class WettbewerbeResource {

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	KlassenstufeService klassenstufeService;

	// @formatter:off
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
	// @formatter:on

		List<Wettbewerb> wettbewerbe = wettbewerbService.loadWettbewerbe();

		return Response.ok(wettbewerbe).build();

	}

	// @formatter:off
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

	// @formatter:off
	@Path("{jahr}/{klassenstufe}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	@Operation(
		operationId = "getStatistikJahrKlassenstufe",
		summary = "Gibt Aufgaben und Statistik der Klassenstufe eines Wettbewerbs zurück",
		description = "Aufgaben und Auswertunsgstatistiken werden nur bei beendeten Wettbewerben gliefert. Unverfängliche Statistiken werden auch im laufenden Wettbewerb geliefert.")
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
			schema = @Schema(implementation = KlassenstufeDetails.class)))
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
	public Response getStatistikJahrKlassenstufe(
		@Pattern(regexp = MkbizaRegexps.VALID_JAHR, message = MkbizaRegexps.MSG_INVALID_JAHR) @PathParam(value = "jahr") final String jahr,
		@PathParam(value = "klassenstufe") final Klassenstufe klassenstufe) {
	// @formatter:on

		KlassenstufeDetails aufgaben = klassenstufeService.loadKlassenstufenDetails(jahr, klassenstufe);

		return Response.ok(aufgaben).build();
	}
}
