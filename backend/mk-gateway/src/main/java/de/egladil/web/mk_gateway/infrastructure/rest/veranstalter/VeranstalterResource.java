// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.security.Principal;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.PrivatveranstalterAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.UserAPIModel;
import de.egladil.web.mk_gateway.domain.auth.AuthResult;
import de.egladil.web.mk_gateway.domain.auth.signup.AuthResultToResourceOwnerMapper;
import de.egladil.web.mk_gateway.domain.auth.signup.SignUpResourceOwner;
import de.egladil.web.mk_gateway.domain.auth.signup.SignUpService;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.veranstalter.LehrerService;
import de.egladil.web.mk_gateway.domain.veranstalter.PrivatpersonService;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagenService;

/**
 * VeranstalterResource ist die Resource zu den Minikänguru-Veranstaltern.
 */
@RequestScoped
@Path("/veranstalter")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VeranstalterResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Context
	SecurityContext securityContext;

	@Inject
	ZugangUnterlagenService zugangUnterlagenService;

	@Inject
	PrivatpersonService privatpersonService;

	@Inject
	LehrerService lehrerService;

	@Inject
	SignUpService signUpService;

	@Inject
	AuthResultToResourceOwnerMapper authResultMapper;

	@Inject
	Event<SecurityIncidentRegistered> securityIncidentEvent;

	private SecurityIncidentRegistered securityIncidentRegistered;

	@POST
	@Path("")
	public Response createUser(final AuthResult authResult) {

		SignUpResourceOwner signUpResourceOwner = authResultMapper.apply(authResult);

		signUpService.createUser(signUpResourceOwner, false);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(applicationMessages.getString("createUser.success"))))
			.build();
	}

	@PUT
	@Path("")
	public Response updateUser(final UserAPIModel user) {

		// die wird vom authprovider aufgerufen, wenn ein Benutzer sein Profil ändert (Name, Vorname) oder konto löscht, dann update
		// mit anonymisiertem Namen
		// dann müssen die Schulkollegien aktualisiert werden!
		// Vor allem muss ein Event erzeugt und gespeichert und gefeuert werden.
		return Response.status(987).entity(ResponsePayload.messageOnly(MessagePayload.error("API ist noch nicht fertig"))).build();

	}

	@GET
	@Path("/zugangsstatus")
	public Response getStatusZugangUnterlagen() {

		final String principalName = securityContext.getUserPrincipal().getName();
		boolean hat = zugangUnterlagenService.hatZugang(principalName);

		return Response.ok(new ResponsePayload(MessagePayload.ok(), Boolean.valueOf(hat))).build();

	}

	@GET
	@Path("/privat")
	public Response getPrivatveranstalter() {

		Principal principal = securityContext.getUserPrincipal();

		PrivatveranstalterAPIModel privatveranstalter = privatpersonService.findPrivatperson(principal.getName());
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), privatveranstalter);

		return Response.ok(responsePayload).build();

	}

	SecurityIncidentRegistered getSecurityIncidentRegistered() {

		return securityIncidentRegistered;
	}

}
