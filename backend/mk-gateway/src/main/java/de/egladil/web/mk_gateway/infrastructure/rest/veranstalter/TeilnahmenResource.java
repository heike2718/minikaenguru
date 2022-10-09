// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.util.List;

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
import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulanmeldungRequestPayload;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * AnonymisierteTeilnahmenResource
 */
@RequestScoped
@Path("teilnahmen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TeilnahmenResource {

	@Context
	SecurityContext securityContext;

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	@Inject
	AuthorizationService authService;

	@Inject
	AnonymisierteTeilnahmenService anonTeilnahmenService;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("veranstalter/{teilnahmenummer}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAnonymisierteTeilnahmen(@PathParam(value = "teilnahmenummer") final String teilnahmenummer) {

		this.delayService.pause();

		List<AnonymisierteTeilnahmeAPIModel> teilnahmen = anonTeilnahmenService.loadAnonymisierteTeilnahmen(teilnahmenummer,
			securityContext.getUserPrincipal().getName());

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), teilnahmen);
		return Response.ok(responsePayload).build();
	}

	@POST
	@Path("privat")
	public Response meldePrivatmenschZumAktuellenWettbewerbAn() {

		this.delayService.pause();

		final String principalName = securityContext.getUserPrincipal().getName();

		ResponsePayload responsePayload = this.aktuelleTeilnahmeService.privatpersonAnmelden(principalName);

		return Response
			.ok(responsePayload)
			.build();
	}

	@POST
	@Path("schule")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meldeSchuleZumAktuellenWettbewerbAn(final SchulanmeldungRequestPayload payload) {

		this.delayService.pause();

		final String principalName = securityContext.getUserPrincipal().getName();

		ResponsePayload responsePayload = aktuelleTeilnahmeService.schuleAnmelden(payload, principalName);

		return Response
			.ok(responsePayload)
			.build();
	}

}
