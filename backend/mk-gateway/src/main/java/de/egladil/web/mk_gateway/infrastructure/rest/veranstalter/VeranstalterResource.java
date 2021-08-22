// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.security.Principal;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.auth.signup.AuthResultToResourceOwnerMapper;
import de.egladil.web.mk_gateway.domain.auth.signup.SignUpService;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.veranstalter.ChangeNewsletterAboService;
import de.egladil.web.mk_gateway.domain.veranstalter.LehrerService;
import de.egladil.web.mk_gateway.domain.veranstalter.PrivatveranstalterService;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagenService;
import de.egladil.web.mk_gateway.domain.veranstalter.api.PrivatveranstalterAPIModel;

/**
 * VeranstalterResource ist die Resource zu den Minikänguru-Veranstaltern.
 */
@RequestScoped
@Path("veranstalter")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VeranstalterResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Context
	SecurityContext securityContext;

	@Inject
	ZugangUnterlagenService zugangUnterlagenService;

	@Inject
	PrivatveranstalterService privatveranstalterService;

	@Inject
	ChangeNewsletterAboService changeNewsletterAboService;

	@Inject
	LehrerService lehrerService;

	@Inject
	SignUpService signUpService;

	@Inject
	AuthResultToResourceOwnerMapper authResultMapper;

	private SecurityIncidentRegistered securityIncidentRegistered;

	@PUT
	@Path("newsletter")
	public Response changeStatusNewsletter() {

		Veranstalter veranstalter = this.changeNewsletterAboService
			.changeStatusNewsletter(securityContext.getUserPrincipal().getName());

		String msg = veranstalter.isNewsletterEmpfaenger() ? applicationMessages.getString("registerForNewsletterSuccess")
			: applicationMessages.getString("deregisterForNewsletterSuccess");

		return Response.ok()
			.entity(ResponsePayload.messageOnly(MessagePayload.info(msg)))
			.build();

	}

	@GET
	@Path("privat")
	public Response getPrivatveranstalter() {

		Principal principal = securityContext.getUserPrincipal();

		PrivatveranstalterAPIModel privatveranstalter = privatveranstalterService.findPrivatperson(principal.getName());
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), privatveranstalter);

		return Response.ok(responsePayload).build();

	}

	SecurityIncidentRegistered getSecurityIncidentRegistered() {

		return securityIncidentRegistered;
	}

}
