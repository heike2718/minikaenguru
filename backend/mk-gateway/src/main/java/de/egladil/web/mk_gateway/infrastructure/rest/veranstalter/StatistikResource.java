// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import de.egladil.web.mk_gateway.infrastructure.rest.general.statistik.PersonalizedStatisticsResourceDelegate;

/**
 * StatistikResource
 */
@RequestScoped
@Path("statistik")
@Consumes(MediaType.APPLICATION_JSON)
public class StatistikResource {

	@Context
	SecurityContext securityContext;

	@Inject
	PersonalizedStatisticsResourceDelegate statisticsResourceDelegate;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("{teilnahmeart}/{teilnahmenummer}/{jahr}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public Response downloadStatistik(@PathParam(value = "teilnahmeart") final String teilnahmeart, @PathParam(
		value = "teilnahmenummer") final String teilnahmenummer, @PathParam(value = "jahr") final String jahr) {

		this.delayService.pause();

		DownloadData data = this.statisticsResourceDelegate.erstelleStatistikPDFEinzelteilnahme(teilnahmeart, teilnahmenummer, jahr,
			securityContext.getUserPrincipal().getName());

		return MkGatewayFileUtils.createDownloadResponse(data);
	}

}
