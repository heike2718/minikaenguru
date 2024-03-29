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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.kinder.AdminKinderService;
import de.egladil.web.mk_gateway.domain.loesungszettel.AdminLoesungszettelService;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Gruppeninfo;
import de.egladil.web.mk_gateway.domain.unterlagen.AdminDownloadsService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import de.egladil.web.mk_gateway.infrastructure.rest.general.statistik.PersonalizedStatisticsResourceDelegate;

/**
 * AdminStatistikResource
 */
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Path("admin/statistik")
@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
public class AdminStatistikResource {

	@Context
	SecurityContext securityContext;

	@Inject
	PersonalizedStatisticsResourceDelegate statisticsResourceDelegate;

	@Inject
	AdminKinderService adminKinderService;

	@Inject
	AdminLoesungszettelService adminLoesungszettelService;

	@Inject
	AdminDownloadsService adminDownloadsService;

	@Inject
	DevDelayService delayService;

	// TODO: path und auth
	public Response getStatistikFuerOrt() {

		return null;
	}

	// TODO: path und auth
	public Response getStatistikFuerLand() {

		return null;
	}

	@GET
	@Path("{teilnahmeart}/{teilnahmenummer}/{jahr}")
	public Response downloadStatistikFuerTeilnahme(@PathParam(value = "teilnahmeart") final String teilnahmeart, @PathParam(
		value = "teilnahmenummer") final String teilnahmenummer, @PathParam(value = "jahr") final String jahr) {

		this.delayService.pause();

		DownloadData data = this.statisticsResourceDelegate.erstelleStatistikPDFEinzelteilnahme(teilnahmeart, teilnahmenummer, jahr,
			securityContext.getUserPrincipal().getName());

		return MkGatewayFileUtils.createDownloadResponse(data);
	}

	@GET
	@Path("kinder")
	public Response getKurzstatistikKinder() {

		this.delayService.pause();

		Gruppeninfo gruppeninfo = adminKinderService.createKurzstatistikKinder();
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), gruppeninfo);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("loesungszettel")
	public Response getKurzstatistikLoesungszettel() {

		this.delayService.pause();

		Gruppeninfo gruppeninfo = adminLoesungszettelService.createKurzstatistikLoesungszettel();
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), gruppeninfo);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("downloads")
	public Response getKurzstatistikDownloads() {

		this.delayService.pause();

		Gruppeninfo gruppeninfo = adminDownloadsService.createKurzstatistikDownloads();
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), gruppeninfo);

		return Response.ok(responsePayload).build();
	}

}
