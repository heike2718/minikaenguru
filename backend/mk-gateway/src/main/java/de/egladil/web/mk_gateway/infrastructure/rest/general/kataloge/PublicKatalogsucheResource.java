// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general.kataloge;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulkatalogAntrag;
import de.egladil.web.mk_gateway.domain.kataloge.dto.KatalogItem;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * PublicKatalogsucheResource
 */
@RequestScoped
@Path("schulkatalog")
@Produces(MediaType.APPLICATION_JSON)
public class PublicKatalogsucheResource {

	@Inject
	MkKatalogeResourceAdapter katalogResourceAdapter;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("suche/{typ}")
	@Operation(
		operationId = "findItems", summary = "Gibt alle KatalogItems vom Typ typ zurück, die auf die gegebene Suchanfrage passen.")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "typ",
			required = true,
			description = "Katalogtyp: LAND, ORT, SCHULE"),
		@Parameter(
			in = ParameterIn.QUERY,
			required = true,
			name = "search", description = "Suchstring, mit dem nach KatalogItems im Namen gesucht wird."),
	})
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	@APIResponse(
		name = "BadRequest",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response findItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		this.delayService.pause();

		return katalogResourceAdapter.findItems(typ, searchTerm);
	}

	@GET
	@Path("suche/laender/{land}/orte")
	@Operation(
		operationId = "findOrteInLand", summary = "Gibt alle Orte im gegebenen Land zurück, deren Name mit dem Suchstring beginnt.")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "land",
			required = true,
			description = "Kürzel des Lands im Schulkatalog"),
		@Parameter(
			in = ParameterIn.QUERY,
			name = "search",
			required = true,
			description = "Anfangsbuchstaben des Ortsnamens"),
	})
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = ResponsePayload.class)))
	@APIResponse(
		name = "BadRequest",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response findOrteInLand(@LandKuerzel @PathParam(
		value = "land") final String landKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		this.delayService.pause();

		return katalogResourceAdapter.findOrteInLand(landKuerzel, searchTerm);
	}

	@GET
	@Path("suche/orte/{ort}/schulen")
	@Operation(
		operationId = "findSchulenInOrt", summary = "Gibt alle Schulen im gegebenen Ort zurück, deren Name den Suchstring enthält.")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "ort",
			required = true,
			description = "Kürzel des Orts im Schulkatalog"),
		@Parameter(
			in = ParameterIn.QUERY,
			name = "search",
			required = true,
			description = "Teil des Schulnamens"),
	})
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = ResponsePayload.class)))
	@APIResponse(
		name = "BadRequest",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response findSchulenInOrt(@Kuerzel @PathParam(
		value = "ort") final String ortKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		this.delayService.pause();

		return katalogResourceAdapter.findSchulenInOrt(ortKuerzel, searchTerm);

	}

	@GET
	@Path("orte/{ort}/schulen")
	@Operation(
		operationId = "loadSchulenInOrt", summary = "Läd die Schulen des Schulkatalogs, die im gegebenen Ort liegen.")
	@Parameters({
		@Parameter(name = "kuerzel", in = ParameterIn.PATH, description = "Kürzel des Orts im Schulkatalog", required = true) })
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	@APIResponse(
		name = "BadRequest",
		description = "Wenn die Treffermenge größer als die konfigurierte maximale Anzahl ist (default = 25).",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response loadSchulenInOrt(@Kuerzel @PathParam(
		value = "ort") final String ortKuerzel) {

		this.delayService.pause();

		return katalogResourceAdapter.loadSchulenInOrt(ortKuerzel);
	}

	@GET
	@Path("laender/{land}/orte")
	@Operation(
		operationId = "loadOrteInLand", summary = "Läd die Orte des Schulkatalogs, die im gegebenen Land liegen.")
	@Parameters({
		@Parameter(name = "kuerzel", in = ParameterIn.PATH, description = "Kürzel des Lands im Schulkatalog", required = true) })
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	@APIResponse(
		name = "BadRequest",
		description = "Wenn die Treffermenge größer als die konfigurierte maximale Anzahl ist (default = 25).",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response loadOrteInLand(@LandKuerzel @PathParam(
		value = "land") final String landKuerzel) {

		this.delayService.pause();

		return katalogResourceAdapter.loadOrteInLand(landKuerzel);

	}

	@POST
	@Path("katalogantrag")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
		operationId = "submitSchule", summary = "Sendet die Daten des Schulantragsformulars an die konfgurierte Mailadresse.")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "BadRequest",
		description = "bei Fehlern in der Input-Validierung",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response sendeKatalogantrag(final SchulkatalogAntrag antrag) {

		this.delayService.pause();

		return this.katalogResourceAdapter.sendeSchulkatalogAntrag(antrag);
	}
}
