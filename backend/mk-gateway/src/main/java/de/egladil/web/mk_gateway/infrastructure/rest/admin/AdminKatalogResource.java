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
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
import de.egladil.web.mk_gateway.domain.kataloge.api.LandPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.OrtPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;
import de.egladil.web.mk_gateway.domain.kataloge.dto.KatalogItem;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
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
	public Response loadLaender() {

		this.delayService.pause();

		return katalogResourceAdapter.loadLaender(securityContext.getUserPrincipal().getName(), katalogAdminSecret);
	}

	@GET
	@Path("laender/{kuerzel}/orte")
	public Response loadOrteInLand(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		this.delayService.pause();

		return katalogResourceAdapter.loadOrteInLand(kuerzel);
	}

	@GET
	@Path("orte/{kuerzel}/schulen")
	public Response loadSchulenInOrt(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		this.delayService.pause();

		return katalogResourceAdapter.loadSchulenInOrt(kuerzel);
	}

	@PUT
	@Path("laender")
	public Response renameLand(final LandPayload requestPayload) {

		this.delayService.pause();

		String uuid = securityContext.getUserPrincipal().getName();
		return katalogResourceAdapter.renameLand(uuid, katalogAdminSecret, requestPayload);
	}

	@PUT
	@Path("orte")
	public Response renameOrt(final OrtPayload requestPayload) {

		this.delayService.pause();

		String uuid = securityContext.getUserPrincipal().getName();

		LOG.debug("Start: uuid={}, {}", uuid, requestPayload);
		return katalogResourceAdapter.renameOrt(uuid, katalogAdminSecret, requestPayload);
	}

	@PUT
	@Path("schulen")
	public Response renameSchule(final SchulePayload requestPayload) {

		this.delayService.pause();

		String uuid = securityContext.getUserPrincipal().getName();
		return this.schulkatalogService.renameSchule(uuid, katalogAdminSecret, requestPayload);

	}

	@POST
	@Path("schulen")
	public Response createSchule(final SchulePayload requestPayload) {

		this.delayService.pause();

		String uuid = securityContext.getUserPrincipal().getName();
		return katalogResourceAdapter.createSchule(uuid, katalogAdminSecret, requestPayload);

	}

	@GET
	@Path("suche/laender/{kuerzel}/orte")
	@Operation(
		operationId = "sucheOrteInLand",
		summary = "Gibt alle Orte im gegebenen Land zurück, deren Name mit dem Suchstring beginnt.")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "kuerzel",
			description = "Kürzel des Landes"),
		@Parameter(
			in = ParameterIn.QUERY,
			name = "search", description = "Anfang des Ortsnamens"),
	})
	@APIResponse(
		name = "FindRaetselAdminOKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	public Response sucheOrteInLand(@PathParam(
		value = "kuerzel") @LandKuerzel final String kuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		this.delayService.pause();

		return katalogResourceAdapter.findOrteInLand(kuerzel, searchTerm);
	}

	@GET
	@Path("suche/orte/{kuerzel}/schulen")
	@Operation(
		operationId = "sucheSchuleInOrt",
		summary = "Gibt alle Schulen im gegebenen Ort zurück, deren Name den Suchstring enthält.")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "kuerzel",
			description = "Kürzel des Ortes"),
		@Parameter(
			in = ParameterIn.QUERY,
			name = "search", description = "Teil des Schulnamens"),
	})
	@APIResponse(
		name = "FindRaetselAdminOKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	public Response sucheSchuleInOrt(@PathParam(
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
			description = "Katalogtyp: LAND, ORT, SCHULE"),
		@Parameter(
			in = ParameterIn.QUERY,
			name = "search", description = "Suchstring, mit dem nach KatalogItems im Namen gesucht wird."),
	})
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	public Response sucheItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		this.delayService.pause();

		return katalogResourceAdapter.sucheItems(typ, searchTerm);
	}

	@GET
	@Path("kuerzel")
	public Response generateKuerzel() {

		this.delayService.pause();

		return katalogResourceAdapter.generateKuerzel(katalogAdminSecret);
	}
}
