// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.event.EventService;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;

/**
 * AdminEventlogRespource
 */
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
@Path("admin/events")
public class AdminEventlogRespource {

	@Inject
	EventService eventService;

	/**
	 * @param  dateString
	 *                    String Tagesdatum im Format yyyy-MM-dd
	 * @return            Response
	 */
	@GET
	@Path("{ab}")
	public Response getEventLogFromDate(@PathParam(value = "ab") final String dateString) {

		DownloadData downloadData = eventService.exportEventsStartingFromDate(dateString);
		return MkGatewayFileUtils.createDownloadResponse(downloadData);

	}
}
