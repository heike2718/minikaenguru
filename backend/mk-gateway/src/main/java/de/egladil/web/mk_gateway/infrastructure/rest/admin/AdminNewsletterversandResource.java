// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.ErrorResponseDto;
import de.egladil.web.mk_gateway.domain.newsletterversand.NewsletterVersandauftragService;
import de.egladil.web.mk_gateway.domain.newsletterversand.api.NewsletterVersandauftrag;
import de.egladil.web.mk_gateway.domain.newsletterversand.api.VersandauftragDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * AdminNewsletterversandResource
 */
@RequestScoped
@Path("admin/versandauftraege")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminNewsletterversandResource {

	@Inject
	NewsletterVersandauftragService newsletterVersandauftragService;

	@GET
	@Operation(
		operationId = "loadVersandauftraege",
		summary = "Läd alle Versandaufträge sortiert nach Erstellungsdatum.")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = VersandauftragDTO.class)))
	@APIResponse(
		name = "NotAuthorized",
		responseCode = "401",
		content = @Content(
			mediaType = "application/json"))
	@APIResponse(
		name = "ServerError",
		description = "server error",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ErrorResponseDto.class)))
	public Response loadVersandauftraege() {

		List<VersandauftragDTO> result = this.newsletterVersandauftragService
			.loadAll();

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), result);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("/{versandauftragID}")
	@Operation(
		operationId = "getVersandinfo",
		summary = "Gibt die Versandinformation zurück, also den Status des Versands eines NewsletterVersandauftrags.")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = VersandauftragDTO.class)))
	@APIResponse(
		name = "BadRequestResponse",
		responseCode = "400",
		description = "fehlgeschlagene Input-Validierung",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ErrorResponseDto.class)))
	@APIResponse(
		name = "NotAuthorized",
		responseCode = "401",
		content = @Content(
			mediaType = "application/json"))
	@APIResponse(
		name = "NotFound",
		responseCode = "404")
	@APIResponse(
		name = "ServerError",
		description = "server error",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ErrorResponseDto.class)))
	public Response getVersandauftrag(@UuidString @PathParam(value = "versandauftragID") final String versandauftragID) {

		Optional<VersandauftragDTO> optVersandInfo = this.newsletterVersandauftragService
			.getStatusNewsletterVersand(versandauftragID);

		if (optVersandInfo.isEmpty()) {

			return Response
				.ok(ResponsePayload.messageOnly(MessagePayload.warn("Keine Versandinfo mehr verfuegbar - Versand abgeschlossen?")))
				.build();

		}

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), optVersandInfo.get());

		return Response.ok(responsePayload).build();
	}

	@POST
	@Operation(
		operationId = "scheduleNewsletterversand",
		summary = "Erstellt einen Newsletterversandauftrag mit hinreichend vielen Auslieferungen (Gruppen von Mailempfängern). Die Aufträge werden durch einen Scheduler abgearbeitet. Das kann viele Stunden dauern wegen der Beschränkungen durch den Provider.")
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "BadRequestResponse",
		responseCode = "400",
		description = "fehlgeschlagene Input-Validierung",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "NotAuthorized",
		responseCode = "401",
		content = @Content(
			mediaType = "application/json"))
	@APIResponse(
		name = "NotFound",
		responseCode = "404",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	@APIResponse(
		name = "ServerError",
		description = "server error",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response scheduleNewsletterversand(@Valid final NewsletterVersandauftrag auftrag) {

		// Versandauftrag versandauftrag = newsletterVersandauftragService.createVersandauftrag(auftrag);
		// ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("Newsletterversand erfolgreich beauftragt"),
		// VersandauftragDTO.createFromVersandauftrag(versandauftrag));

		ResponsePayload responsePayload = newsletterVersandauftragService.createVersandauftrag(auftrag);
		return Response.ok(responsePayload).build();
	}
}
