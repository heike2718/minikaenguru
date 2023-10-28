// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

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

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.KatalogAPIApp;
import de.egladil.web.mk_kataloge.application.KatalogFacade;
import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.admin.CreateSchuleService;
import de.egladil.web.mk_kataloge.domain.admin.RenameLandService;
import de.egladil.web.mk_kataloge.domain.admin.RenameOrtService;
import de.egladil.web.mk_kataloge.domain.admin.RenameSchuleService;
import de.egladil.web.mk_kataloge.domain.apimodel.LandPayload;
import de.egladil.web.mk_kataloge.domain.apimodel.OrtPayload;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.domain.event.SecurityIncidentRegistered;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * KatalogeResource
 */
@ApplicationScoped
@Path("kataloge")
@Produces(MediaType.APPLICATION_JSON)
public class KatalogeResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(KatalogeResource.class);

	@ConfigProperty(name = "admin.secret")
	String expectedSecret;

	@ConfigProperty(name = "maximaleAnzahlTreffer", defaultValue = "25")
	int maximaleAnzahlTreffer;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	@Inject
	KatalogFacade katalogFacade;

	@Inject
	CreateSchuleService createSchuleService;

	@Inject
	RenameSchuleService renameSchuleService;

	@Inject
	RenameOrtService renameOrtService;

	@Inject
	RenameLandService renameLandService;

	@Inject
	LoggableEventDelegate eventDelegate;

	@GET
	@Path("laender")
	@Operation(
		operationId = "loadLaender", summary = "Läd die Länder des Schulkatalogs.")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	public Response loadLaender(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret) {

		if (!expectedSecret.equals(secret)) {

			String msg = "Unautorisierter Versuch, die Länder zu laden: angemeldeter ADMIN=" + adminUuid + ", secret=" + secret;

			LOGGER.warn(msg);

			eventDelegate.fireSecurityEvent(msg, securityEvent);

			return Response.status(Status.FORBIDDEN)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("Netter Versuch, aber leider keine Berechtigung")))
				.build();

		}

		List<KatalogItem> result = katalogFacade.loadLaender();

		return Response.ok(new ResponsePayload(MessagePayload.ok(), result)).build();
	}

	/**
	 * Ändert den Namen und evtl das Kürzel des gegebenen Landes und gibt das geänderte LandPayload zurück. Bei einem
	 * konkurrierenden Update wird
	 * das aktuell neueste Item zurückgegeben, damit es sofort im Editor angezeigt werden kann.
	 *
	 * @param  adminUuid
	 *                        String: die UUID des angemeldeten ADMINs
	 * @param  secret
	 *                        String teilen sich mk-gateway und mk-kataloge, da mk-kataloge keine Benutzerverwaltung hat.
	 * @param  requestPayload
	 *                        LandPayload
	 * @return                Response mit LandPayload
	 */
	@PUT
	@Path("laender")
	@Operation(
		operationId = "updateLand", summary = "Ändert die Daten eines Landes im Schulkatalog")
	@Parameters({
		@Parameter(name = "X-UUID", in = ParameterIn.HEADER, description = "UUID des Admins", required = true),
		@Parameter(name = "X-SECRET", in = ParameterIn.HEADER, description = "ein secret", required = true) })
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
		description = "wenn im Header X-SECRET was falsches steht.",
		responseCode = "403",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response updateLand(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret, @Valid final LandPayload requestPayload) {

		if (!expectedSecret.equals(secret)) {

			String msg = "Unautorisierter Versuch, das Land " + requestPayload.kuerzel()
				+ " umzubenennen: angemeldeter ADMIN=" + adminUuid + ", secret="
				+ secret;

			LOGGER.warn(msg);

			eventDelegate.fireSecurityEvent(msg, securityEvent);

			return Response.status(Status.FORBIDDEN)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("Netter Versuch, aber leider keine Berechtigung")))
				.build();

		}

		ResponsePayload responsePayload = renameLandService.updateLand(requestPayload);

		LOGGER.info("ADMIN {} hat Land {} umbenannt: Erfolg = {}", StringUtils.abbreviate(adminUuid, 11), requestPayload,
			responsePayload.getMessage().getMessage());

		return Response.ok(responsePayload).build();
	}

	/**
	 * Benennt den Ort im gegebenen Land um und gibt das geänderte OrtPayload zurück. Bei einem konkurrierenden Update wird
	 * das aktuell neueste Item zurückgegeben, damit es sofort im Editor angezeigt werden kann.
	 *
	 * @param  adminUuid
	 *                        String: die UUID des angemeldeten ADMINs
	 * @param  secret
	 *                        String teilen sich mk-gateway und mk-kataloge, da mk-kataloge keine Benutzerverwaltung hat.
	 * @param  requestPayload
	 *                        OrtPayload
	 * @return                Response mit OrtPayload
	 */
	@PUT
	@Path("orte")
	@Operation(
		operationId = "renameOrt", summary = "Ändert den Namen des Orts im Schulkatalog")
	@Parameters({
		@Parameter(name = "X-UUID", in = ParameterIn.HEADER, description = "UUID des Admins", required = true),
		@Parameter(name = "X-SECRET", in = ParameterIn.HEADER, description = "ein secret", required = true) })
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
		description = "wenn im Header X-SECRET was falsches steht.",
		responseCode = "403",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response renameOrt(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret, @Valid final OrtPayload requestPayload) {

		if (!expectedSecret.equals(secret)) {

			String msg = "Unautorisierter Versuch, den Ort " + requestPayload.kuerzel()
				+ " umzubenennen: angemeldeter ADMIN=" + adminUuid + ", secret="
				+ secret;

			LOGGER.warn(msg);

			eventDelegate.fireSecurityEvent(msg, securityEvent);

			return Response.status(Status.FORBIDDEN)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("Netter Versuch, aber leider keine Berechtigung")))
				.build();

		}

		ResponsePayload responsePayload = renameOrtService.ortUmbenennen(requestPayload);

		LOGGER.info("ADMIN {} hat Ort {} umbenannt: Erfolg = {}", StringUtils.abbreviate(adminUuid, 11), requestPayload,
			responsePayload.getMessage().getMessage());

		return Response.ok(responsePayload).build();
	}

	/**
	 * Benennt die gegebene Schule um und gibt sie als SchulePayload zurück. Bei einem konkurrierenden Update wird
	 * das aktuell neueste Item zurückgegeben, damit es sofort im Editor angezeigt werden kann.
	 *
	 * @param  adminUuid
	 *                        String: die UUID des angemeldeten ADMINs
	 * @param  secret
	 *                        String teilen sich mk-gateway und mk-kataloge, da mk-kataloge keine Benutzerverwaltung hat.
	 * @param  requestPayload
	 *                        SchulePayload
	 * @return                Response mit SchulePayload
	 */
	@PUT
	@Path("schulen")
	@Operation(
		operationId = "renameSchule", summary = "Ändert den Namen der Schule im Schulkatalog")
	@Parameters({
		@Parameter(name = "X-UUID", in = ParameterIn.HEADER, description = "UUID des Admins", required = true),
		@Parameter(name = "X-SECRET", in = ParameterIn.HEADER, description = "ein secret", required = true) })
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
		description = "wenn im Header X-SECRET was falsches steht.",
		responseCode = "403",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response renameSchule(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret, @Valid final SchulePayload requestPayload) {

		if (!expectedSecret.equals(secret)) {

			String msg = "Unautorisierter Versuch, die Schule " + requestPayload.kuerzel()
				+ " umzubenennen: angemeldeter ADMIN=" + adminUuid + ", secret="
				+ secret;

			LOGGER.warn(msg);

			eventDelegate.fireSecurityEvent(msg, securityEvent);

			return Response.status(Status.FORBIDDEN)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("Netter Versuch, aber leider keine Berechtigung")))
				.build();

		}

		ResponsePayload responsePayload = renameSchuleService.schuleUmbenennen(requestPayload);

		LOGGER.info("ADMIN {} hat Schule {} umbenannt: Erfolg = {}", StringUtils.abbreviate(adminUuid, 11), requestPayload,
			responsePayload.getMessage().getMessage());

		return Response.ok(responsePayload).build();
	}

	/**
	 * Legt eine neue Schule an, ggf. mit neuem Ort / Land und gibt die neue Schule so zurück. Bei einem konkurrierenden Update wird
	 * das aktuell neueste Item zurückgegeben, damit es sofort im Editor angezeigt werden kann.
	 *
	 * @param  adminUuid
	 *                        String: die UUID des angemeldeten ADMINs
	 * @param  secret
	 *                        String teilen sich mk-gateway und mk-kataloge, da mk-kataloge keine Benutzerverwaltung hat.
	 * @param  requestPayload
	 *                        SchulePayload
	 * @return                Response mit SchulePayload
	 */
	@POST
	@Path("schulen")
	@Operation(
		operationId = "createSchule", summary = "Legt eine neue Schule an")
	@Parameters({
		@Parameter(name = "X-UUID", in = ParameterIn.HEADER, description = "UUID des Admins", required = true),
		@Parameter(name = "X-SECRET", in = ParameterIn.HEADER, description = "ein secret", required = true) })
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
		description = "wenn im Header X-SECRET was falsches steht.",
		responseCode = "403",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response createSchule(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret, @Valid final SchulePayload requestPayload) {

		if (!expectedSecret.equals(secret)) {

			String msg = "Unautorisierter Versuch, eine Schule anzulegen: angemeldeter ADMIN=" + adminUuid + ", secret=" + secret;

			LOGGER.warn(msg);

			eventDelegate.fireSecurityEvent(msg, securityEvent);

			return Response.status(Status.FORBIDDEN)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("Netter Versuch, aber leider keine Berechtigung")))
				.build();

		}

		ResponsePayload responsePayload = createSchuleService.schuleAnlegen(requestPayload);

		LOGGER.info("ADMIN {} hat Schule {} angelegt: Erfolg = {}", StringUtils.abbreviate(adminUuid, 11), requestPayload,
			responsePayload.getMessage().getMessage());

		return Response.ok(responsePayload).build();
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

		int anzahlTreffer = katalogFacade.countOrteInLand(kuerzel);

		LOGGER.info("land={}, Anzahl Orte={}", kuerzel, anzahlTreffer);

		if (anzahlTreffer > maximaleAnzahlTreffer) {

			ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload
				.warn("Die Abfrage ergibt zu viele Treffer. Bitte verwenden Sie die Suche /mk-kataloge-api/katalogsuche/laender/"
					+ kuerzel + "/orte?search="));

			return Response.status(Status.BAD_REQUEST).entity(responsePayload).build();
		}

		List<KatalogItem> trefferliste = katalogFacade.loadOrteInLand(kuerzel);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), trefferliste);

		return Response.ok(responsePayload).build();
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

		int anzahlTreffer = katalogFacade.countSchulenInOrt(kuerzel);

		if (anzahlTreffer > maximaleAnzahlTreffer) {

			ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload
				.warn("Die Abfrage ergibt zu viele Treffer. Bitte verwenden Sie die Suche /mk-kataloge-api/katalogsuche/orte/"
					+ kuerzel + "/schulen?search="));

			return Response.status(Status.BAD_REQUEST).entity(responsePayload).build();
		}

		List<KatalogItem> trefferliste = katalogFacade.loadSchulenInOrt(kuerzel);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), trefferliste);

		return Response.ok(responsePayload).build();
	}

}
