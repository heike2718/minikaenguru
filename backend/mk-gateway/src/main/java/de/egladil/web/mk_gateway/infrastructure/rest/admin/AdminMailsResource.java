// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
