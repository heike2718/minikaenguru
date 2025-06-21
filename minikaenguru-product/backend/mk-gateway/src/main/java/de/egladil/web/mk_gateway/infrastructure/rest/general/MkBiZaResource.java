// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.statistik.mkbiza.MkBiZaStatistikKlassenstufe;
import de.egladil.web.mk_gateway.domain.statistik.mkbiza.MkBiZaStatistikService;
import de.egladil.web.mk_gateway.domain.statistik.mkbiza.MkBiZaWettbewerb;
import de.egladil.web.mk_gateway.domain.statistik.mkbiza.MkBiZaWettbewerbDetails;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * MkBiZaResource
 */
@RequestScoped
@Path("mkbiza")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MkBiZaResource {

	@Inject
	MkBiZaStatistikService statistikService;

	@Path("wettbewerbe")
	@GET
	@Operation(
		operationId = "getWettbewerbe",
		summary = "Gibt die Daten aller Wettbewerbe zurück, die für jahresübergreifende Statistiken nützlich sind.")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = MkBiZaWettbewerb.class)))
	@APIResponse(
		name = "ServerError",
		description = "Serverfehler",
		responseCode = "500",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	public Response getWettbewerbe() {

		List<MkBiZaWettbewerb> wettbewerbe = statistikService.loadWettbewerbeOverview();

		return Response.ok(wettbewerbe).build();

	}

	@GET
	@Path("wettbewerbe/{jahr}")
	@Operation(
		operationId = "getStatistikWettbewerb",
		summary = "Gibt die Statistik eines Wettbewerbs zurück")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "jahr",
			description = "Jahr des Wettbewerbs",
			required = true) })
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = MkBiZaWettbewerbDetails.class)))
	@APIResponse(
		name = "Unauthorized",
		description = "S2S-Autentifizierung schlug fehl",
		responseCode = "401",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	@APIResponse(
		name = "NotFound",
		description = "Jahr existsiert nicht oder Wettbewerb ist noch nicht beendet",
		responseCode = "404",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	@APIResponse(
		name = "Unprocessable Entity",
		description = "keine Daten für das Jahr",
		responseCode = "422",
		content = @Content(schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "ServerError",
		description = "Serverfehler",
		responseCode = "500",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	public Response getStatistikWettbewerb(@NotNull @PathParam(value = "jahr") final Integer wettbewerbsjahr) {

		MkBiZaWettbewerbDetails responsePayload = statistikService.getStatistikJahr(wettbewerbsjahr);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("wettbewerbe/{jahr}/{klasse}")
	@Operation(
		operationId = "getStatistikJahrKlassenstufe",
		summary = "Gibt die Statistik für eine Klassenstufe eines Wettbewerbs zurück")
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
			required = true) })
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = MkBiZaStatistikKlassenstufe.class)))
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
		name = "Unprocessable Entity",
		description = "keine Daten für das Jahr",
		responseCode = "422",
		content = @Content(schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "ServerError",
		description = "Serverfehler",
		responseCode = "500",
		content = @Content(schema = @Schema(implementation = MessagePayload.class)))
	// @formatter:off
	public Response getStatistikJahrKlassenstufe(
		@NotNull @PathParam(value = "jahr") final Integer wettbewerbsjahr,
		@NotNull @PathParam(value = "klasse") final Klassenstufe klassenstufe) {
	// @formatter:on

		MkBiZaStatistikKlassenstufe responsePayload = statistikService.getStatistikJahrKlassenstufe(wettbewerbsjahr, klassenstufe);

		return Response.ok(responsePayload).build();
	}

}
