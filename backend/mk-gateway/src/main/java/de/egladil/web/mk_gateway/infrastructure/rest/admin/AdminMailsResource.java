// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.mail.MailToVeranstalterService;
import de.egladil.web.mk_gateway.domain.mail.api.MailAPIModel;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * AdminMailsResource
 */
@RequestScoped
@Path("admin/mails")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminMailsResource {

	@Inject
	MailToVeranstalterService mailVeranstalterService;

	@Inject
	DevDelayService delayService;

	@POST
	public Response sendeMail(final MailAPIModel mailModel) {

		this.delayService.pause();

		ResponsePayload responsePayload = mailVeranstalterService.sendeMailAnVeranstalter(mailModel);

		return Response.ok(responsePayload).build();
	}

}
