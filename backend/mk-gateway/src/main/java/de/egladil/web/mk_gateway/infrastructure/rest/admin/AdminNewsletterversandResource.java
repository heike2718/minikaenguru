// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.Optional;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminNewsletterversandResource.class);

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

		ResponsePayload responsePayload = newsletterService.scheduleAndStartMailversand(auftrag);

		if (!responsePayload.isOk()) {

			LOGGER.info(responsePayload.getMessage().toString());
		}

		return Response.ok(responsePayload).build();
	}

}
