// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang3.tuple.Pair;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.AuswertungImportService;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.UploadAuswertungContext;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.uploadmonitoring.api.UploadMonitoringInfo;
import de.egladil.web.mk_gateway.domain.uploadmonitoring.api.UploadMonitoringService;
import de.egladil.web.mk_gateway.domain.uploads.MultipartUtils;
import de.egladil.web.mk_gateway.domain.uploads.UploadData;
import de.egladil.web.mk_gateway.domain.uploads.UploadManager;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * AdminUploadResource
 */
@RequestScoped
@Path("admin/uploads")
public class AdminUploadResource {

	@Context
	SecurityContext securityContext;

	@Inject
	UploadManager uploadManager;

	@Inject
	AuswertungImportService klassenlisteImportService;

	@Inject
	UploadMonitoringService uploadMonitoringService;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("size")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUploadsCount() {

		this.delayService.pause();

		Long result = uploadMonitoringService.countUploads();

		return Response.ok().entity(new ResponsePayload(MessagePayload.ok(), result)).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUploads(@QueryParam(value = "limit") final int limit, @QueryParam(
		value = "offset") final int offset) {

		this.delayService.pause();

		List<UploadMonitoringInfo> uploads = uploadMonitoringService.loadUploads(limit, offset);
		return Response.ok().entity(new ResponsePayload(MessagePayload.ok(), uploads)).build();
	}

	@POST
	@Path("auswertung/{jahr}/{kuerzelLand}/{schulkuerzel}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadAuswertung(@PathParam(value = "jahr") final Integer jahr, @PathParam(
		value = "kuerzelLand") @LandKuerzel final String kuerzelLand, @PathParam(
			value = "schulkuerzel") @Kuerzel final String schulkuerzel, @QueryParam(
				value = "sprache") @NotBlank final String sprache, final MultipartFormDataInput input) {

		this.delayService.pause();

		String benutzerUuid = securityContext.getUserPrincipal().getName();
		UploadType uploadType = UploadType.AUSWERTUNG;

		Sprache theSprache = Sprache.valueOf(sprache);

		Pair<Rolle, Wettbewerb> rolleUndWettbewerb = uploadManager.authorizeUpload(benutzerUuid, schulkuerzel, uploadType,
			new WettbewerbID(jahr));

		UploadAuswertungContext contextObject = new UploadAuswertungContext().withKuerzelLand(kuerzelLand)
			.withWettbewerb(rolleUndWettbewerb.getRight()).withSprache(theSprache).withRolle(rolleUndWettbewerb.getLeft());

		UploadData uploadData = MultipartUtils.getUploadData(input);

		UploadRequestPayload uploadPayload = new UploadRequestPayload().withTeilnahmenummer(schulkuerzel)
			.withBenutzerID(new Identifier(benutzerUuid)).withUploadType(uploadType).withUploadData(uploadData)
			.withContext(contextObject);

		ResponsePayload responsePayload = uploadManager.processUpload(uploadPayload);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("{uuid}/fehlerreport")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON }) // text/plain, damit man kein encoding-Problem bekommt
	public Response downloadFehlerreport(@PathParam(value = "uuid") final String uuid) {

		this.delayService.pause();

		DownloadData downloadData = this.uploadMonitoringService.getFehlerReport(uuid);
		return MkGatewayFileUtils.createDownloadResponse(downloadData);
	}

	@GET
	@Path("{uuid}/file")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON }) // text/plain, damit man kein encoding-Problem bekommt
	public Response downloadUploadedFile(@PathParam(value = "uuid") final String uuid) {

		this.delayService.pause();

		DownloadData downloadData = this.uploadMonitoringService.getUploadedFile(uuid);
		return MkGatewayFileUtils.createDownloadResponse(downloadData);
	}
}
