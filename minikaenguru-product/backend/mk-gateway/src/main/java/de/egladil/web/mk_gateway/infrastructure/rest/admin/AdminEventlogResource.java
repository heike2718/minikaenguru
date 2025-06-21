// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.event.EventService;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * AdminEventlogRespource
 */
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON }) // text/plain, damit man kein encoding-Problem bekommt
@Path("admin/events")
public class AdminEventlogResource {

	@Inject
	EventService eventService;

	/**
	 * @param  dateString
	 *                    String Tagesdatum im Format yyyy-MM-dd
	 * @return            Response
	 */
	@GET
	@Path("{ab}")
	@Operation(
		operationId = "getEventLogBeginningWithDate",
		summary = "Gibt die Daten des Eventlogs ab einem gegebenen Datum zurück.")
	@Parameters({
		@Parameter(name = "ab", in = ParameterIn.PATH, description = "Tagesdatum im Format yyyy-MM-dd", required = true) })
	@APIResponse(
		name = "OKResponse",
		description = "Eventlog erfolgreich erstellt",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = DownloadData.class)))
	public Response getEventLogBeginningWithDate(@PathParam(value = "ab") final String dateString) {

		DownloadData downloadData = eventService.exportEventsStartingFromDate(dateString);
		return MkGatewayFileUtils.createDownloadResponse(downloadData);

	}
}
