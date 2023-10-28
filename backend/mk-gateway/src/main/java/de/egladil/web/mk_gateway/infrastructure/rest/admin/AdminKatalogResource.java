// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
import de.egladil.web.mk_gateway.domain.kataloge.api.KuerzelAPIModel;
import de.egladil.web.mk_gateway.domain.kataloge.api.LandPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.OrtPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;
import de.egladil.web.mk_gateway.domain.kataloge.dto.KatalogItem;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * AdminKatalogResource
 */
@RequestScoped
@Path("admin/kataloge")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminKatalogResource {

	private static final Logger LOG = LoggerFactory.getLogger(AdminKatalogResource.class);

	@Context
	SecurityContext securityContext;

	@ConfigProperty(name = "admin.secret")
	String katalogAdminSecret;

	@Inject
	MkKatalogeResourceAdapter katalogResourceAdapter;

	@Inject
	SchulkatalogService schulkatalogService;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("laender")
	@Operation(
		operationId = "loadLaender", summary = "Läd die Länder des Schulkatalogs.")
	@APIResponse(
		name = "loadLaenderOKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	public Response loadLaender() {

		this.delayService.pause();

		return katalogResourceAdapter.loadLaender(securityContext.getUserPrincipal().getName(), katalogAdminSecret);
	}

	@GET
	@Path("laender/{kuerzel}/orte")
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
	public Response loadOrteInLand(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		this.delayService.pause();

		return katalogResourceAdapter.loadOrteInLand(kuerzel);
	}

	@GET
	@Path("orte/{kuerzel}/schulen")
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
	public Response loadSchulenInOrt(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		this.delayService.pause();

		return katalogResourceAdapter.loadSchulenInOrt(kuerzel);
	}

	@PUT
	@Path("laender")
	@Operation(
		operationId = "renameLand", summary = "Ändert die Daten eines Landes im Schulkatalog")
	@APIResponse(
		name = "loadLaenderOKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "Bad Request",
		description = "Input-Validierung",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "Forbidden",
		description = "ist Konfigurationsfehler beim Aufruf der kataloge-OpenAPI",
		responseCode = "403",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response renameLand(@Valid final LandPayload requestPayload) {

		this.delayService.pause();

		String uuid = securityContext.getUserPrincipal().getName();
		return katalogResourceAdapter.renameLand(uuid, katalogAdminSecret, requestPayload);
	}

	@PUT
	@Path("orte")
	@Operation(
		operationId = "renameOrt", summary = "Ändert den Namen des Orts im Schulkatalog")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "Bad Request",
		description = "Input-Validierung",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "Forbidden",
		description = "ist Konfigurationsfehler beim Aufruf der kataloge-OpenAPI",
		responseCode = "403",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response renameOrt(@Valid final OrtPayload requestPayload) {

		this.delayService.pause();

		String uuid = securityContext.getUserPrincipal().getName();

		LOG.debug("Start: uuid={}, {}", uuid, requestPayload);
		return katalogResourceAdapter.renameOrt(uuid, katalogAdminSecret, requestPayload);
	}

	@PUT
	@Path("schulen")
	@Operation(
		operationId = "renameSchule", summary = "Ändert den Namen der Schule im Schulkatalog")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "Bad Request",
		description = "Input-Validierung",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "Forbidden",
		description = "ist Konfigurationsfehler beim Aufruf der kataloge-OpenAPI",
		responseCode = "403",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response renameSchule(@Valid final SchulePayload requestPayload) {

		this.delayService.pause();

		String uuid = securityContext.getUserPrincipal().getName();
		return this.schulkatalogService.renameSchule(uuid, katalogAdminSecret, requestPayload);

	}

	@POST
	@Path("schulen")
	@Operation(
		operationId = "createSchule", summary = "Legt eine neue Schule an und sendet eine Erfolgsmail an den Auftraggeber.")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "Bad Request",
		description = "Input-Validierung",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "Forbidden",
		description = "ist Konfigurationsfehler beim Aufruf der kataloge-OpenAPI",
		responseCode = "403",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response createSchule(@Valid final SchulePayload requestPayload) {

		this.delayService.pause();

		String uuid = securityContext.getUserPrincipal().getName();
		return katalogResourceAdapter.createSchule(uuid, katalogAdminSecret, requestPayload);

	}

	@GET
	@Path("suche/laender/{kuerzel}/orte")
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
	public Response findOrteInLand(@PathParam(
		value = "kuerzel") @LandKuerzel final String kuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		this.delayService.pause();

		return katalogResourceAdapter.findOrteInLand(kuerzel, searchTerm);
	}

	@GET
	@Path("suche/orte/{kuerzel}/schulen")
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
	public Response findSchulenInOrt(@PathParam(
		value = "kuerzel") @Kuerzel final String kuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		this.delayService.pause();

		return katalogResourceAdapter.findSchulenInOrt(kuerzel, searchTerm);
	}

	@GET
	@Path("suche/global/{typ}")
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

		return katalogResourceAdapter.sucheItems(typ, searchTerm);
	}

	@GET
	@Path("kuerzel")
	@Operation(
		operationId = "generateKuerzelFuerSchuleUndOrt",
		summary = "Generiert zwei neue Kürzel, eins für den Ort, eins für die Schule.")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = KuerzelAPIModel.class)))
	@APIResponse(
		name = "Forbidden",
		description = "wenn im Header X-SECRET was falsches steht.",
		responseCode = "403",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response generateKuerzel() {

		this.delayService.pause();

		return katalogResourceAdapter.generateKuerzel(katalogAdminSecret);
	}
}
