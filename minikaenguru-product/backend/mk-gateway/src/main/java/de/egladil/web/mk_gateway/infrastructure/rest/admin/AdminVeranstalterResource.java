// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.veranstalter.admin.VeranstalterNewsletterService;
import de.egladil.web.mk_gateway.domain.veranstalter.admin.VeranstalterSucheService;
import de.egladil.web.mk_gateway.domain.veranstalter.admin.VeranstalterZugangsstatusService;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterUserAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.api.ZugangsstatusPayload;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * AdminVeranstalterResource
 */
@RequestScoped
@Path("admin/veranstalter")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminVeranstalterResource {

	@Inject
	VeranstalterSucheService veranstalterSucheService;

	@Inject
	VeranstalterZugangsstatusService veranstalterZugangsstatusService;

	@Inject
	VeranstalterNewsletterService veranstalterNewsletterService;

	@Inject
	DevDelayService delayService;

	@POST
	@Path("suche")
	public Response findVeranstalter(final VeranstalterSuchanfrage suchanfrage) {

		this.delayService.pause();

		List<VeranstalterUserAPIModel> treffer = veranstalterSucheService.findVeranstalter(suchanfrage);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), treffer);
		return Response.ok(responsePayload).build();
	}

	@POST
	@Path("{uuidPrefix}/zugangsstatus")
	public Response changeZugangsstatus(@PathParam(
		value = "uuidPrefix") final String uuidPrefix, final ZugangsstatusPayload zugangsstatus) {

		this.delayService.pause();

		ResponsePayload responsePayload = this.veranstalterZugangsstatusService.aendereVeranstalter(uuidPrefix,
			zugangsstatus.getZugangsstatus());

		return Response.ok(responsePayload).build();
	}

	@POST
	@Path("{uuidPrefix}/newsletter")
	public Response deactivateNewsletter(@PathParam(
		value = "uuidPrefix") final String uuidPrefix) {

		this.delayService.pause();

		ResponsePayload responsePayload = this.veranstalterNewsletterService.aendereVeranstalter(uuidPrefix, null);

		return Response.ok(responsePayload).build();
	}

}
