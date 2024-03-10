// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

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
