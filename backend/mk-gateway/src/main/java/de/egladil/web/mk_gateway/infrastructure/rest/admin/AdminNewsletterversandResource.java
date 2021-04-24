// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.mail.NewsletterService;
import de.egladil.web.mk_gateway.domain.mail.VersandinfoService;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterVersandauftrag;
import de.egladil.web.mk_gateway.domain.mail.api.VersandinfoAPIModel;

/**
 * AdminNewsletterversandResource
 */
@RequestScoped
@Path("admin/newsletterversand")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminNewsletterversandResource {

	@Inject
	NewsletterService newsletterService;

	@Inject
	VersandinfoService versandinfoService;

	@GET
	@Path("/{versandinfoUuid}")
	public Response getVersandinfo(@UuidString @PathParam(value = "versandinfoUuid") final String versandinfoUuid) {

		Optional<VersandinfoAPIModel> optVersandInfo = this.versandinfoService.getStatusNewsletterVersand(versandinfoUuid);

		if (optVersandInfo.isEmpty()) {

			return Response
				.ok(ResponsePayload.messageOnly(MessagePayload.warn("Keine Versandinfo mehr verfuegbar - Versand abgeschlossen?")))
				.build();

		}

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), optVersandInfo.get());

		return Response.ok(responsePayload).build();
	}

	@POST
	public Response scheduleNewsletterversand(final NewsletterVersandauftrag auftrag) {

		VersandinfoAPIModel data = newsletterService.scheduleAndStartMailversand(auftrag);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), data);

		return Response.ok(responsePayload).build();
	}

}
