// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

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

import org.apache.commons.lang3.tuple.Pair;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteImportService;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.uploads.MultipartUtils;
import de.egladil.web.mk_gateway.domain.uploads.UploadData;
import de.egladil.web.mk_gateway.domain.uploads.UploadManager;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;

/**
 * UploadResource
 */
@RequestScoped
@Path("uploads")
@Produces(MediaType.APPLICATION_JSON)
public class UploadResource {

	@Context
	SecurityContext securityContext;

	@Inject
	UploadManager uploadManager;

	@Inject
	KlassenlisteImportService klassenlisteImportService;

	@POST
	@Path("klassenlisten/{kuerzelLand}/{schulkuerzel}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	Response uploadKlassenliste(@PathParam(value = "kuerzelLand") @LandKuerzel final String kuerzelLand, @PathParam(
		value = "schulkuerzel") @Kuerzel final String schulkuerzel, @QueryParam(
			value = "nachnameAlsZusatz") final boolean nachnameAlsZusatz, @QueryParam(
				value = "sprache") @NotBlank final String sprache, final MultipartFormDataInput input) {

		String veranstalterUuid = securityContext.getUserPrincipal().getName();
		UploadType uploadType = UploadType.KLASSENLISTE;

		Sprache theSprache = Sprache.valueOf(sprache);

		Pair<Rolle, Wettbewerb> rolleUndWettbewerb = uploadManager.authorizeUpload(veranstalterUuid, schulkuerzel, uploadType,
			null);

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

}
