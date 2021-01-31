// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.statistik.AnonymisierteTeilnahmenService;
import de.egladil.web.mk_gateway.domain.teilnahmen.AktuelleTeilnahmeService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.PrivatteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulanmeldungRequestPayload;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulteilnahmeAPIModel;

/**
 * AnonymisierteTeilnahmenResource
 */
@RequestScoped
@Path("teilnahmen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TeilnahmenResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Context
	SecurityContext securityContext;

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	@Inject
	AuthorizationService authService;

	@Inject
	AnonymisierteTeilnahmenService anonTeilnahmenService;

	@GET
	@Path("veranstalter/{teilnahmenummer}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAnonymisierteTeilnahmen(@PathParam(value = "teilnahmenummer") final String teilnahmenummer) {

		List<AnonymisierteTeilnahmeAPIModel> teilnahmen = anonTeilnahmenService.loadAnonymisierteTeilnahmen(teilnahmenummer,
			securityContext.getUserPrincipal().getName());

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), teilnahmen);
		return Response.ok(responsePayload).build();
	}

	@POST
	@Path("privat")
	public Response meldePrivatmenschZumAktuellenWettbewerbAn() {

		final String principalName = securityContext.getUserPrincipal().getName();

		Privatteilnahme teilnahme = this.aktuelleTeilnahmeService.privatpersonAnmelden(principalName);

		// hier könnte noch über Messging ein Ereignis propagiert werden, um en update eines Anmeldungszählers zu triggern

		return Response
			.ok(new ResponsePayload(
				MessagePayload.info(applicationMessages.getString("teilnahmenResource.anmelden.privat.success")),
				PrivatteilnahmeAPIModel.createFromPrivatteilnahme(teilnahme)))
			.build();
	}

	@POST
	@Path("schule")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meldeSchuleZumAktuellenWettbewerbAn(final SchulanmeldungRequestPayload payload) {

		final String principalName = securityContext.getUserPrincipal().getName();

		SchulteilnahmeAPIModel data = aktuelleTeilnahmeService.schuleAnmelden(payload, principalName);

		String message = MessageFormat.format(applicationMessages.getString("teilnahmenResource.anmelden.schule.success"),
			new Object[] { data.nameUrkunde() });

		// hier könnte noch über Messging ein Ereignis propagiert werden, um den update eines Anmeldungszählers zu triggern

		return Response
			.ok(new ResponsePayload(
				MessagePayload.info(message),
				data))
			.build();
	}

}
