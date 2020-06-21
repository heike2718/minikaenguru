// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.rest;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb.MkvServerApp;
import de.egladil.web.mk_wettbewerb.domain.apimodel.TeilnahmeAPIModel;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.AktuelleTeilnahmeService;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Privatteilnahme;

/**
 * TeilnahmenResource
 */
@Path("/teilnahmen")
@Produces(MediaType.APPLICATION_JSON)
public class TeilnahmenResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	@POST
	@Path("/privat")
	public Response privatpersonZumWettbewerbAnmelden(@HeaderParam(
		value = MkvServerApp.UUID_HEADER_NAME) final String principalName) {

		Privatteilnahme teilnahme = this.aktuelleTeilnahmeService.privatpersonAnmelden(principalName);

		return Response
			.ok(new ResponsePayload(
				MessagePayload.info(applicationMessages.getString("teilnahmenResource.anmelden.privat.success")),
				TeilnahmeAPIModel.create(teilnahme)))
			.build();
	}

}
