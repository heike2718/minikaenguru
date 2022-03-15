// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.NewsletterService;
import de.egladil.web.mk_gateway.domain.mail.api.NewsletterAPIModel;

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

	@GET
	public Response loadNewsletters() {

		List<NewsletterAPIModel> newsletters = this.newsletterService.getAllNewsletters();

		return Response.ok(new ResponsePayload(MessagePayload.ok(), newsletters)).build();
	}

	@POST
	public Response addNewsletter(final NewsletterAPIModel newsletter) {

		return insertOrUpdateNewsletter(newsletter);
	}

	@PUT
	public Response changeNewsletter(final NewsletterAPIModel newsletter) {

		return insertOrUpdateNewsletter(newsletter);

	}

	@DELETE
	@Path("{newsletterID}")
	public Response deleteNewsletter(@UuidString @PathParam(value = "newsletterID") final String newsletterID) {

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
