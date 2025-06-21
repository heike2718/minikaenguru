// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

import de.egladil.web.commons_validation.InvalidProperty;
import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.kataloge.KuerzelGeneratorService;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
import de.egladil.web.mk_gateway.domain.kataloge.api.KuerzelAPIModel;
import de.egladil.web.mk_gateway.domain.kataloge.api.LandPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.OrtPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;
import de.egladil.web.mk_gateway.domain.kataloge.dto.KatalogItem;
import de.egladil.web.mk_gateway.domain.kataloge.dto.Katalogtyp;
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
import jakarta.ws.rs.core.Response.Status;
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

	@Deprecated
	@ConfigProperty(name = "admin.secret")
	String katalogAdminSecret;

	// @Inject
	// MkKatalogeResourceAdapter katalogResourceAdapter;

	@Inject
	SchulkatalogService schulkatalogService;

	@Inject
	KuerzelGeneratorService kuerzelGeneratorService;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("laender")
	@Operation(operationId = "loadLaender", summary = "Läd die Länder des Schulkatalogs.")
	@APIResponse(name = "admin.loadLaenderOKResponse", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	public Response loadLaender() {

		this.delayService.pause();

		List<KatalogItem> laender = schulkatalogService.loadLaender();

		return Response.ok(laender).build();
	}

	@GET
	@Path("laender/{kuerzel}/orte")
	@Operation(operationId = "admin.loadOrteInLand", summary = "Läd die Orte des Schulkatalogs, die im gegebenen Land liegen.")
	@Parameters({
		@Parameter(name = "kuerzel", in = ParameterIn.PATH, description = "Kürzel des Lands im Schulkatalog", required = true) })
	@APIResponse(name = "OKResponse", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	@APIResponse(name = "BadRequest", description = "Wenn die Treffermenge größer als die konfigurierte maximale Anzahl ist (default = 25).", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	public Response loadOrteInLand(@PathParam(value = "kuerzel")
	@Kuerzel
	final String kuerzel) {

		this.delayService.pause();
		List<KatalogItem> result = schulkatalogService.loadOrteInLand(kuerzel);
		return Response.ok(result).build();
	}

	@GET
	@Path("orte/{kuerzel}/schulen")
	@Operation(operationId = "admin.loadSchulenInOrt", summary = "Läd die Schulen des Schulkatalogs, die im gegebenen Ort liegen.")
	@Parameters({
		@Parameter(name = "kuerzel", in = ParameterIn.PATH, description = "Kürzel des Orts im Schulkatalog", required = true) })
	@APIResponse(name = "OKResponse", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	@APIResponse(name = "BadRequest", description = "Wenn die Treffermenge größer als die konfigurierte maximale Anzahl ist (default = 25).", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	public Response loadSchulenInOrt(@PathParam(value = "kuerzel")
	@Kuerzel
	final String kuerzel) {

		this.delayService.pause();
		List<KatalogItem> result = schulkatalogService.loadSchulenInOrt(kuerzel);
		return Response.ok(result).build();
	}

	@PUT
	@Path("laender")
	@Operation(operationId = "admin.renameLand", summary = "Ändert die Daten eines Landes im Schulkatalog")
	@APIResponse(name = "loadLaenderOKResponse", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(name = "Bad Request", description = "Input-Validierung", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(name = "Forbidden", description = "ist Konfigurationsfehler beim Aufruf der kataloge-OpenAPI", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	public Response renameLand(@Valid
	final LandPayload requestPayload) {

		this.delayService.pause();

		ResponsePayload payload = schulkatalogService.landUmbenennen(requestPayload);
		return Response.ok(payload).build();
	}

	@PUT
	@Path("orte")
	@Operation(operationId = "admin.renameOrt", summary = "Ändert den Namen des Orts im Schulkatalog")
	@APIResponse(name = "OKResponse", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(name = "Bad Request", description = "Input-Validierung", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(name = "Forbidden", description = "ist Konfigurationsfehler beim Aufruf der kataloge-OpenAPI", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	public Response renameOrt(@Valid
	final OrtPayload requestPayload) {

		this.delayService.pause();

		ResponsePayload payload = schulkatalogService.ortUmbenennen(requestPayload);
		return Response.ok(payload).build();
	}

	@PUT
	@Path("schulen")
	@Operation(operationId = "admin.renameSchule", summary = "Ändert den Namen der Schule im Schulkatalog")
	@APIResponse(name = "OKResponse", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(name = "Bad Request", description = "Input-Validierung", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(name = "Forbidden", description = "ist Konfigurationsfehler beim Aufruf der kataloge-OpenAPI", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	public Response renameSchule(@Valid
	final SchulePayload requestPayload) {

		this.delayService.pause();

		// RBAC wäre besser
		// String adminUUID = securityContext.getUserPrincipal().getName();
		return Response.ok(this.schulkatalogService.renameSchule(requestPayload)).build();

	}

	@POST
	@Path("schulen")
	@Operation(operationId = "admin.createSchule", summary = "Legt eine neue Schule an und sendet eine Erfolgsmail an den Auftraggeber.")
	@APIResponse(name = "OKResponse", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(name = "Bad Request", description = "Input-Validierung", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(name = "Forbidden", description = "ist Konfigurationsfehler beim Aufruf der kataloge-OpenAPI", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	public Response createSchule(@Valid
	final SchulePayload requestPayload) {

		this.delayService.pause();

		ResponsePayload payload = schulkatalogService.schuleAnlegen(requestPayload);
		return Response.status(201).entity(payload).build();
	}

	@GET
	@Path("suche/laender/{kuerzel}/orte")
	@Operation(operationId = "admin.findOrteInLand", summary = "Gibt alle Orte im gegebenen Land zurück, deren Name mit dem Suchstring beginnt.")
	@Parameters({
		@Parameter(in = ParameterIn.PATH, name = "land", required = true, description = "Kürzel des Lands im Schulkatalog"),
		@Parameter(in = ParameterIn.QUERY, name = "search", required = true, description = "Anfangsbuchstaben des Ortsnamens"), })
	@APIResponse(name = "OKResponse", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	@APIResponse(name = "BadRequest", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	public Response findOrteInLand(@PathParam(value = "kuerzel")
	@LandKuerzel
	final String kuerzel, @NotBlank
	@StringLatin
	@QueryParam("search")
	final String searchTerm) {

		this.delayService.pause();
		List<KatalogItem> result = schulkatalogService.findOrteInLand(kuerzel, searchTerm);
		return Response.ok(result).build();
	}

	@GET
	@Path("suche/orte/{kuerzel}/schulen")
	@Operation(operationId = "admin.findSchulenInOrt", summary = "Gibt alle Schulen im gegebenen Ort zurück, deren Name den Suchstring enthält.")
	@Parameters({ @Parameter(in = ParameterIn.PATH, name = "ort", required = true, description = "Kürzel des Orts im Schulkatalog"),
		@Parameter(in = ParameterIn.QUERY, name = "search", required = true, description = "Teil des Schulnamens"), })
	@APIResponse(name = "OKResponse", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	@APIResponse(name = "BadRequest", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	public Response findSchulenInOrt(@PathParam(value = "kuerzel")
	@Kuerzel
	final String kuerzel, @NotBlank
	@StringLatin
	@QueryParam("search")
	final String searchTerm) {

		this.delayService.pause();

		List<KatalogItem> result = schulkatalogService.findSchulenInOrt(kuerzel, searchTerm);
		return Response.ok(result).build();
	}

	@GET
	@Path("suche/global/{typ}")
	@Operation(operationId = "admin.findItems", summary = "Gibt alle KatalogItems vom Typ typ zurück, die auf die gegebene Suchanfrage passen.")
	@Parameters({ @Parameter(in = ParameterIn.PATH, name = "typ", required = true, description = "Katalogtyp: LAND, ORT, SCHULE"),
		@Parameter(in = ParameterIn.QUERY, required = true, name = "search", description = "Suchstring, mit dem nach KatalogItems im Namen gesucht wird."), })
	@APIResponse(name = "OKResponse", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	@APIResponse(name = "BadRequest", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	public Response findItems(@PathParam(value = "typ")
	final String typ, @NotBlank
	@StringLatin
	@QueryParam("search")
	final String searchTerm) {

		this.delayService.pause();

		Response response = validateSearchTerm(searchTerm);

		if (response != null) {

			return response;
		}

		Katalogtyp katalogtyp = Katalogtyp.valueOf(typ.toUpperCase());

		List<KatalogItem> result = new ArrayList<>();

		switch (katalogtyp) {

		case SCHULE:
			result = schulkatalogService.sucheSchulenMitNameEnthaltend(searchTerm);
			break;

		case ORT:
			result = schulkatalogService.sucheOrteMitNameBeginnendMit(searchTerm);
			break;

		case LAND:
			result = schulkatalogService.sucheLaenderMitNameBeginnendMit(searchTerm);
			break;

		default:
			String msg = "Aufruf von findItems mit unerwartetem Katalogtyp " + typ + ": geben leeres result zurück";
			LOG.warn(msg);
			ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload.error(msg));
			return Response.status(Status.NOT_FOUND).entity(responsePayload).build();
		}

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), result);
		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("kuerzel")
	@Operation(operationId = "admin.generateKuerzelFuerSchuleUndOrt", summary = "Generiert zwei neue Kürzel, eins für den Ort, eins für die Schule.")
	@APIResponse(name = "OKResponse", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KuerzelAPIModel.class)))
	@APIResponse(name = "Forbidden", description = "wenn im Header X-SECRET was falsches steht.", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsePayload.class)))
	public Response generateKuerzel() {

		this.delayService.pause();

		KuerzelAPIModel data = kuerzelGeneratorService.generateKuerzel();
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), data);
		return Response.ok(responsePayload).build();
	}

	/**
	 * @param landKuerzel
	 * @param searchTerm
	 */
	private Response validateSearchTerm(final String searchTerm) {

		if (StringUtils.isBlank(searchTerm)) {

			ResponsePayload payload = new ResponsePayload(MessagePayload.error("Die Eingaben sind nicht korrekt."),
				Arrays.asList(
					new InvalidProperty[] { new InvalidProperty("searchTerm", "darf nicht leer sein", 0) }));

			return Response.status(400)
				.entity(payload)
				.build();
		}

		if (searchTerm.length() > 100) {

			ResponsePayload payload = new ResponsePayload(MessagePayload.error("Die Eingaben sind nicht korrekt."),
				Arrays.asList(
					new InvalidProperty[] { new InvalidProperty("searchTerm", "darf nicht länger als 100 Zeichen sein", 0) }));

			return Response.status(400)
				.entity(payload)
				.build();
		}

		return null;
	}
}
