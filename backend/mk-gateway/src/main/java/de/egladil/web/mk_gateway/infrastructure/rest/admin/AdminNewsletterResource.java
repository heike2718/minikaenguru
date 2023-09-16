// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.NewsletterService;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterAPIModel;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * AdminNewsletterResource
 */
@RequestScoped
@Path("admin/newsletters")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminNewsletterResource {

	@Inject
	NewsletterService newsletterService;

	@Inject
	DevDelayService delayService;

	@GET
	public Response loadNewsletters() {

		this.delayService.pause();

		List<NewsletterAPIModel> newsletters = this.newsletterService.getAllNewsletters();

		return Response.ok(new ResponsePayload(MessagePayload.ok(), newsletters)).build();
	}

	@POST
	public Response addNewsletter(final NewsletterAPIModel newsletter) {

		this.delayService.pause();

		return insertOrUpdateNewsletter(newsletter);
	}

	@PUT
	public Response changeNewsletter(final NewsletterAPIModel newsletter) {

		this.delayService.pause();

		return insertOrUpdateNewsletter(newsletter);

	}

	@DELETE
	@Path("{newsletterID}")
	public Response deleteNewsletter(@UuidString @PathParam(value = "newsletterID") final String newsletterID) {

		this.delayService.pause();

		Identifier identifier = new Identifier(newsletterID);
		this.newsletterService.newsletterLoeschen(identifier);
		return Response
			.ok(ResponsePayload.messageOnly(MessagePayload.info("Newsletter mit ID " + newsletterID + " erolgreich gelösch.")))
			.build();

	}

	private Response insertOrUpdateNewsletter(final NewsletterAPIModel newsletter) {

		NewsletterAPIModel persistiertes = newsletterService.newsletterSpeichern(newsletter);

		ResponsePayload responsePaylod = new ResponsePayload(MessagePayload.ok(), persistiertes);

		return Response.ok(responsePaylod).build();
	}
}
