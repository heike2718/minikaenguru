// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.security.Principal;
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
import de.egladil.web.mk_gateway.domain.apimodel.CreateOrUpdateLehrerCommand;
import de.egladil.web.mk_gateway.domain.apimodel.SchulanmeldungRequestPayload;
import de.egladil.web.mk_gateway.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.auth.AuthResult;
import de.egladil.web.mk_gateway.domain.auth.signup.AuthResultToResourceOwnerMapper;
import de.egladil.web.mk_gateway.domain.auth.signup.SignUpResourceOwner;
import de.egladil.web.mk_gateway.domain.auth.signup.SignUpService;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.wettbewerb.MkWettbewerbResourceAdapter;
import de.egladil.web.mk_gateway.domain.wettbewerb.SchulenAnmeldeinfoService;

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

	@Inject
	MkWettbewerbResourceAdapter mkWettbewerbResourceAdapter;

	@Inject
	SchulenAnmeldeinfoService schulenAnmeldeinfoService;

	@Context
	SecurityContext securityContext;

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

	@Path("/lehrer")
	@PUT
	public Response updateLehrer(final CreateOrUpdateLehrerCommand updateLehrerCommand) {

		Principal principal = securityContext.getUserPrincipal();

		if (!updateLehrerCommand.uuid().equals(principal.getName())) {

			String msg = "Unzulaessiger Versuch durch " + principal.getName() + ",  Veranstalter " + updateLehrerCommand.uuid()
				+ " zu ändern.";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new AccessDeniedException();
		}
		// FIXME: das ist eine API, für die man autorisiert sein muss!
		return null;
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

		SchuleAPIModel schule = this.schulenAnmeldeinfoService.getSchuleWithWettbewerbsdetails(schulkuerzel, principal.getName());

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), schule)).build();
		return response;
	}

	@POST
	@Path("/teilnahmen/privat")
	public Response meldePrivatmenschZumAktuellenWettbewerbAn() {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.meldePrivatmenschZumAktuellenWettbewerbAn(principal.getName());
	}

	@POST
	@Path("/teilnahmen/schule")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meldeSchuleZumAktuellenWettbewerbAn(final SchulanmeldungRequestPayload payload) {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.meldeSchuleZumAktuellenWettbewerbAn(payload, principal.getName());
	}

	@GET
	@Path("/zugangsstatus")
	public Response getStatusZugangUnterlagen() {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.getStatusZugangUnterlagen(principal.getName());

	}

	@GET
	@Path("/lehrer")
	public Response getLehrer() {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.getStatusZugangUnterlagen(principal.getName());

	}

	@GET
	@Path("/privat")
	public Response getPrivatveranstalter() {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.getPrivatveranstalter(principal.getName());

	}

	SecurityIncidentRegistered getSecurityIncidentRegistered() {

		return securityIncidentRegistered;
	}

}
