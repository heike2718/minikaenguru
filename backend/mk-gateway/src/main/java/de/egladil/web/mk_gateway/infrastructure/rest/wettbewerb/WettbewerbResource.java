// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.wettbewerb;

import java.security.Principal;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import de.egladil.web.mk_gateway.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.session.LoggedInUser;
import de.egladil.web.mk_gateway.domain.wettbewerb.MkWettbewerbResourceAdapter;
import de.egladil.web.mk_gateway.domain.wettbewerb.SchulenAnmeldeinfoService;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkKatalogeRestException;
import de.egladil.web.mk_gateway.infrastructure.messaging.MkWettbewerbRestException;

/**
 * WettbewerbResource
 */
@RequestScoped
@Path("/wettbewerb")
@Produces(MediaType.APPLICATION_JSON)
public class WettbewerbResource {

	private static final Logger LOG = LoggerFactory.getLogger(WettbewerbResource.class);

	@Inject
	MkWettbewerbResourceAdapter mkWettbewerbResourceAdapter;

	@Inject
	SchulenAnmeldeinfoService schulenAnmeldeinfoService;

	@Context
	SecurityContext securityContext;

	@GET
	@Path("/lehrer/schulen")
	public Response findSchulen() {

		Principal principal = securityContext.getUserPrincipal();

		try {

			List<SchuleAPIModel> schulen = schulenAnmeldeinfoService
				.findSchulenMitAnmeldeinfo(principal.getName());

			ResponsePayload payload = new ResponsePayload(MessagePayload.ok(), schulen);

			return Response.ok(payload).build();
		} catch (MkKatalogeRestException | MkWettbewerbRestException e) {

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

	public Response getAktuelleTeilnahmePrivat() {

		Principal principal = securityContext.getUserPrincipal();

		return null;

	}

	@POST
	@Path("/teilnahmen/schulen/{schulkuerzel}")
	public Response meldeSchuleZumAktuellenWettbewerbAn(@PathParam(value = "schulkuerzel") final String schulkuerzel) {

		LoggedInUser loggedInUser = (LoggedInUser) securityContext.getUserPrincipal();

		LOG.info("Hier jetzt routen zu mk-wettbewerbe mit der uuid und der Rolle des Users {}", loggedInUser);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.warn("Diese Methode ist noch nicht fertig"))).build();
	}

	@GET
	@Path("/aktueller")
	public Response getAktuellenWettbewerb() {

		return mkWettbewerbResourceAdapter.getAktuellenWettbewerb();
	}

	@GET
	@Path("/veranstalter/zugangsstatus")
	public Response getStatusZugangUnterlagen() {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.getStatusZugangUnterlagen(principal.getName());

	}

	@GET
	@Path("/veranstalter/lehrer")
	public Response getLehrer() {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.getStatusZugangUnterlagen(principal.getName());

	}

	@GET
	@Path("/veranstalter/teilnahmeinfos")
	public Response getInitialTeilnahmeinfos() {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.getStatusZugangUnterlagen(principal.getName());

	}
}
