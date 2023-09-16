// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.security.Principal;
import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

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
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

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

	@Inject
	DevDelayService delayService;

	private SecurityIncidentRegistered securityIncidentRegistered;

	@PUT
	@Path("newsletter")
	public Response changeStatusNewsletter() {

		this.delayService.pause();

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

		this.delayService.pause();

		Principal principal = securityContext.getUserPrincipal();

		PrivatveranstalterAPIModel privatveranstalter = privatveranstalterService.findPrivatperson(principal.getName());
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), privatveranstalter);

		return Response.ok(responsePayload).build();

	}

	SecurityIncidentRegistered getSecurityIncidentRegistered() {

		return securityIncidentRegistered;
	}

}
