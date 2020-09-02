// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.security.Principal;
import java.text.MessageFormat;
import java.util.List;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.PrivatteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.PrivatveranstalterAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.SchulanmeldungRequestPayload;
import de.egladil.web.mk_gateway.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.SchulteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.UserAPIModel;
import de.egladil.web.mk_gateway.domain.auth.AuthResult;
import de.egladil.web.mk_gateway.domain.auth.signup.AuthResultToResourceOwnerMapper;
import de.egladil.web.mk_gateway.domain.auth.signup.SignUpResourceOwner;
import de.egladil.web.mk_gateway.domain.auth.signup.SignUpService;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.teilnahmen.AktuelleTeilnahmeService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.veranstalter.LehrerService;
import de.egladil.web.mk_gateway.domain.veranstalter.PrivatpersonService;
import de.egladil.web.mk_gateway.domain.veranstalter.SchulenAnmeldeinfoService;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterAuthorizationService;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagenService;

/**
 * VeranstalterResource ist die Resource zu den Minikänguru-Veranstaltern.
 */
@RequestScoped
@Path("/veranstalter")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VeranstalterResource {

	private static final Logger LOG = LoggerFactory.getLogger(VeranstalterResource.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Context
	SecurityContext securityContext;

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	@Inject
	ZugangUnterlagenService zugangUnterlagenService;

	@Inject
	SchulenAnmeldeinfoService schulenAnmeldeinfoService;

	@Inject
	PrivatpersonService privatpersonService;

	@Inject
	LehrerService lehrerService;

	@Inject
	SignUpService signUpService;

	@Inject
	AuthResultToResourceOwnerMapper authResultMapper;

	@Inject
	VeranstalterAuthorizationService veranstalterAuthService;

	@Inject
	Event<SecurityIncidentRegistered> securityIncidentEvent;

	private SecurityIncidentRegistered securityIncidentRegistered;

	static VeranstalterResource createForPermissionTest(final VeranstalterAuthorizationService veranstalterAuthService, final SecurityContext securityContext) {

		VeranstalterResource result = new VeranstalterResource();
		result.veranstalterAuthService = veranstalterAuthService;
		result.securityContext = securityContext;
		return result;
	}

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
	@Path("/lehrer/schulen")
	public Response findSchulen() {

		Principal principal = securityContext.getUserPrincipal();

		try {

			List<SchuleAPIModel> schulen = schulenAnmeldeinfoService
				.findSchulenMitAnmeldeinfo(principal.getName());

			ResponsePayload payload = new ResponsePayload(MessagePayload.ok(), schulen);

			return Response.ok(payload).build();
		} catch (Exception e) {

			LOG.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException(e.getMessage());
		}
	}

	@GET
	@Path("/lehrer/schulen/{schulkuerzel}/details")
	public Response getSchuleDetails(@PathParam(value = "schulkuerzel") final String schulkuerzel) {

		Principal principal = securityContext.getUserPrincipal();

		final Identifier lehrerID = new Identifier(principal.getName());
		final Identifier schuleID = new Identifier(schulkuerzel);

		veranstalterAuthService.checkPermissionForTeilnahmenummer(lehrerID, schuleID);

		SchuleAPIModel schule = this.schulenAnmeldeinfoService.getSchuleWithWettbewerbsdetails(schulkuerzel, principal.getName());

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), schule)).build();
		return response;
	}

	@POST
	@Path("/teilnahmen/privat")
	public Response meldePrivatmenschZumAktuellenWettbewerbAn() {

		final String principalName = securityContext.getUserPrincipal().getName();

		Privatteilnahme teilnahme = this.aktuelleTeilnahmeService.privatpersonAnmelden(principalName);

		return Response
			.ok(new ResponsePayload(
				MessagePayload.info(applicationMessages.getString("teilnahmenResource.anmelden.privat.success")),
				PrivatteilnahmeAPIModel.createFromPrivatteilnahme(teilnahme)))
			.build();
	}

	@POST
	@Path("/teilnahmen/schule")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meldeSchuleZumAktuellenWettbewerbAn(final SchulanmeldungRequestPayload payload) {

		final String principalName = securityContext.getUserPrincipal().getName();

		Schulteilnahme schulteilnahme = aktuelleTeilnahmeService.schuleAnmelden(payload, principalName);

		String message = MessageFormat.format(applicationMessages.getString("teilnahmenResource.anmelden.schule.success"),
			new Object[] { schulteilnahme.nameSchule() });

		SchulteilnahmeAPIModel data = SchulteilnahmeAPIModel.create(schulteilnahme).withKlassenGeladen(true);

		return Response
			.ok(new ResponsePayload(
				MessagePayload.info(message),
				data))
			.build();
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
