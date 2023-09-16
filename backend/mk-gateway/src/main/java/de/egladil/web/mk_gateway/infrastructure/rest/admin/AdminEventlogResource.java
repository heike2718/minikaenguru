// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.event.EventService;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;

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
	public Response getEventLogBeginningWithDate(@PathParam(value = "ab") final String dateString) {

		DownloadData downloadData = eventService.exportEventsStartingFromDate(dateString);
		return MkGatewayFileUtils.createDownloadResponse(downloadData);

	}
}
