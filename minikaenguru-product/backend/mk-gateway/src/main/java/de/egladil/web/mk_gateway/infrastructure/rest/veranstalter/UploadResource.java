// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import jakarta.enterprise.context.RequestScoped;

import org.apache.commons.lang3.tuple.Pair;
import org.jboss.resteasy.reactive.server.multipart.MultipartFormDataInput;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteImportService;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.UploadAuswertungContext;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.uploads.MultipartUtils;
import de.egladil.web.mk_gateway.domain.uploads.UploadData;
import de.egladil.web.mk_gateway.domain.uploads.UploadManager;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * UploadResource
 */
@RequestScoped
@Path("uploads")
public class UploadResource {

	@Context
	SecurityContext securityContext;

	@Inject
	UploadManager uploadManager;

	@Inject
	KlassenlisteImportService klassenlisteImportService;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	DevDelayService delayService;

	@POST
	@Path("klassenlisten/{jahr}/{kuerzelLand}/{schulkuerzel}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadKlassenliste(@PathParam(value = "jahr") final Integer jahr, @PathParam(
		value = "kuerzelLand") @LandKuerzel final String kuerzelLand, @PathParam(
			value = "schulkuerzel") @Kuerzel final String schulkuerzel, @QueryParam(
				value = "nachnameAlsZusatz") final String nachnameAlsZusatzString, @QueryParam(
					value = "sprache") @NotBlank final String sprache, final MultipartFormDataInput input) {

		this.delayService.pause();

		String veranstalterUuid = securityContext.getUserPrincipal().getName();
		UploadType uploadType = UploadType.KLASSENLISTE;

		Sprache theSprache = Sprache.valueOf(sprache);

		boolean nachnameAlsZusatz = Boolean.valueOf(nachnameAlsZusatzString);

		Pair<Rolle, Wettbewerb> rolleUndWettbewerb = uploadManager.authorizeUpload(veranstalterUuid, schulkuerzel, uploadType,
			new WettbewerbID(jahr));

		UploadData uploadData = MultipartUtils.getUploadData(input);

		UploadKlassenlisteContext contextObject = new UploadKlassenlisteContext().withKuerzelLand(kuerzelLand)
			.withNachnameAlsZusatz(nachnameAlsZusatz).withSprache(theSprache).withRolle(rolleUndWettbewerb.getLeft())
			.withWettbewerb(rolleUndWettbewerb.getRight());

		UploadRequestPayload uploadPayload = new UploadRequestPayload().withTeilnahmenummer(schulkuerzel)
			.withBenutzerID(new Identifier(veranstalterUuid)).withUploadType(uploadType).withUploadData(uploadData)
			.withContext(contextObject);

		ResponsePayload responsePayload = uploadManager.processUpload(uploadPayload);

		return Response.ok(responsePayload).build();
	}

	@POST
	@Path("auswertung/{jahr}/{kuerzelLand}/{schulkuerzel}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadAuswertung(@PathParam(value = "jahr") final Integer jahr, @PathParam(
		value = "kuerzelLand") @LandKuerzel final String kuerzelLand, @PathParam(
			value = "schulkuerzel") @Kuerzel final String schulkuerzel, final MultipartFormDataInput input) {

		this.delayService.pause();

		String veranstalterUuid = securityContext.getUserPrincipal().getName();
		UploadType uploadType = UploadType.AUSWERTUNG;

		Pair<Rolle, Wettbewerb> rolleUndWettbewerb = uploadManager.authorizeUpload(veranstalterUuid, schulkuerzel, uploadType,
			new WettbewerbID(jahr));

		UploadData uploadData = MultipartUtils.getUploadData(input);

		UploadAuswertungContext contextObject = new UploadAuswertungContext().withKuerzelLand(kuerzelLand)
			.withSprache(Sprache.de).withRolle(rolleUndWettbewerb.getLeft())
			.withWettbewerb(rolleUndWettbewerb.getRight());

		UploadRequestPayload uploadPayload = new UploadRequestPayload().withTeilnahmenummer(schulkuerzel)
			.withBenutzerID(new Identifier(veranstalterUuid)).withUploadType(uploadType).withUploadData(uploadData)
			.withContext(contextObject);

		ResponsePayload responsePayload = uploadManager.processUpload(uploadPayload);

		return Response.ok(responsePayload).build();
	}
}
