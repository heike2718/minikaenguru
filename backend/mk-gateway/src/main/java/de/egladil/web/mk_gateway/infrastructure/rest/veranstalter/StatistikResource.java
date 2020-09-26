// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.statistik.StatistikService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * StatistikResource
 */
@RequestScoped
@Path("/statistik")
@Consumes(MediaType.APPLICATION_JSON)
public class StatistikResource {

	@Context
	SecurityContext securityContext;

	@Inject
	StatistikService statistikService;

	@GET
	@Path("{teilnahmeart}/{teilnahmenummer}/{jahr}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public Response downloadStatistik(@PathParam(value = "teilnahmeart") final String teilnahmeart, @PathParam(
		value = "teilnahmenummer") final String teilnahmenummer, @PathParam(value = "jahr") final String jahr) {

		WettbewerbID wettbewerbID = null;

		try {

			wettbewerbID = new WettbewerbID(Integer.valueOf(jahr));

		} catch (NumberFormatException e) {

			throw new BadRequestException("jahr muss numerisch sein.");
		}
		TeilnahmeIdentifier identifier = new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart)
			.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(wettbewerbID);

		DownloadData data = this.statistikService.erstelleStatistikPDFEinzelteilnahme(identifier,
			securityContext.getUserPrincipal().getName());

		return MkGatewayFileUtils.createDownloadResponse(data);
	}

}
