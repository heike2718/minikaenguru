// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulkatalogAntrag;
import de.egladil.web.mk_kataloge.domain.katalogantrag.KatalogAntragService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * KatalogAntragResource
 */
@ApplicationScoped
@Path("katalogantrag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KatalogAntragResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	KatalogAntragService service;

	@POST
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
	public Response submitSchule(final SchulkatalogAntrag antrag) {

		service.validateAndSend(antrag);

		ResponsePayload payload = ResponsePayload
			.messageOnly(MessagePayload.info(applicationMessages.getString("schulkatalogantrag.success")));

		return Response.ok(payload).build();
	}

}
