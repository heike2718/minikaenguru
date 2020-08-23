// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.rest;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb.MkvServerApp;
import de.egladil.web.mk_wettbewerb.domain.apimodel.PrivatteilnahmeAPIModel;
import de.egladil.web.mk_wettbewerb.domain.apimodel.SchulanmeldungRequestPayload;
import de.egladil.web.mk_wettbewerb.domain.apimodel.SchulteilnahmeAPIModel;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.AktuelleTeilnahmeService;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.PrivatteilnahmenMigrationService;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Schulteilnahme;

/**
 * TeilnahmenResource
 */
@Path("/teilnahmen")
@Produces(MediaType.APPLICATION_JSON)
public class TeilnahmenResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	@Inject
	PrivatteilnahmenMigrationService privatteilnahmenMigrationService;

	@POST
	@Path("/privat")
	public Response privatpersonZumWettbewerbAnmelden(@HeaderParam(
		value = MkvServerApp.UUID_HEADER_NAME) final String principalName) {

		Privatteilnahme teilnahme = this.aktuelleTeilnahmeService.privatpersonAnmelden(principalName);

		return Response
			.ok(new ResponsePayload(
				MessagePayload.info(applicationMessages.getString("teilnahmenResource.anmelden.privat.success")),
				PrivatteilnahmeAPIModel.createFromPrivatteilnahme(teilnahme)))
			.build();
	}

	@POST
	@Path("/schule")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response schuleZumWettbewerbAnmelden(final SchulanmeldungRequestPayload payload, @HeaderParam(
		value = MkvServerApp.UUID_HEADER_NAME) final String principalName) {

		Schulteilnahme schulteilnahme = aktuelleTeilnahmeService.schuleAnmelden(payload, principalName);

		String message = MessageFormat.format(applicationMessages.getString("teilnahmenResource.anmelden.schule.success"),
			new Object[] { schulteilnahme.nameSchule() });

		SchulteilnahmeAPIModel data = SchulteilnahmeAPIModel.create(schulteilnahme).withKlassenGeladen(true);

		return Response
			.ok(new ResponsePayload(
				MessagePayload.info(message),
				data))
			.build();
	}

	@GET
	@Path("/import/privat")
	@Deprecated(forRemoval = true)
	public Response triggerImportPrivatteilnahmen() {

		privatteilnahmenMigrationService.run();

		return Response.ok().build();

	}

}
