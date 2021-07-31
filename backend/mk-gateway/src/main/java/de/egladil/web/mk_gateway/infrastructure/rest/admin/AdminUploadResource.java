// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.AuswertungImportService;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.UploadAuswertungContext;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.uploads.MultipartUtils;
import de.egladil.web.mk_gateway.domain.uploads.UploadData;
import de.egladil.web.mk_gateway.domain.uploads.UploadManager;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * AdminUploadResource
 */
@RequestScoped
@Path("admin/uploads")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminUploadResource {

	@Context
	SecurityContext securityContext;

	@Inject
	UploadManager uploadManager;

	@Inject
	AuswertungImportService klassenlisteImportService;

	@POST
	@Path("auswertung/{jahr}/{kuerzelLand}/{schulkuerzel}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	Response uploadKlassenliste(@PathParam(value = "jahr") final Integer jahr, @PathParam(
		value = "kuerzelLand") @LandKuerzel final String kuerzelLand, @PathParam(
			value = "schulkuerzel") @Kuerzel final String schulkuerzel, @QueryParam(
				value = "sprache") @NotBlank final String sprache, final MultipartFormDataInput input) {

		String benutzerUuid = securityContext.getUserPrincipal().getName();
		UploadType uploadType = UploadType.KLASSENLISTE;

		Sprache theSprache = Sprache.valueOf(sprache);

		Rolle rolle = uploadManager.authorizeUpload(benutzerUuid, schulkuerzel, uploadType);

		UploadData uploadData = MultipartUtils.getUploadData(input);

		UploadAuswertungContext contextObject = new UploadAuswertungContext().withKuerzelLand(kuerzelLand)
			.withWettbewerbsjahr(jahr).withSprache(theSprache);

		UploadRequestPayload uploadPayload = new UploadRequestPayload().withTeilnahmenummer(schulkuerzel)
			.withBenutzerID(new Identifier(benutzerUuid)).withUploadType(uploadType).withUploadData(uploadData)
			.withContext(contextObject);

		ResponsePayload responsePayload = uploadManager.processUpload(uploadPayload, rolle);

		return Response.ok(responsePayload).build();
	}
}
