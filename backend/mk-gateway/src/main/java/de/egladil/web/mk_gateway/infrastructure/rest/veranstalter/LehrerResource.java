// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.kinder.KlassenService;
import de.egladil.web.mk_gateway.domain.veranstalter.CheckCanRemoveSchuleService;
import de.egladil.web.mk_gateway.domain.veranstalter.LehrerService;
import de.egladil.web.mk_gateway.domain.veranstalter.SchulenAnmeldeinfoService;
import de.egladil.web.mk_gateway.domain.veranstalter.api.LehrerAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * LehrerResource
 */
@RequestScoped
@Path("lehrer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LehrerResource {

	private static final Logger LOG = LoggerFactory.getLogger(LehrerResource.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Context
	SecurityContext securityContext;

	@Inject
	SchulenAnmeldeinfoService schulenAnmeldeinfoService;

	@Inject
	CheckCanRemoveSchuleService checkRemoveSchuleService;

	@Inject
	LehrerService lehrerService;

	@Inject
	KlassenService klassenService;

	@Inject
	DevDelayService delayService;

	@GET
	public Response getLehrer() {

		this.delayService.pause();

		Principal principal = securityContext.getUserPrincipal();

		LehrerAPIModel lehrer = lehrerService.findLehrer(principal.getName());
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), lehrer);

		return Response.ok(responsePayload).build();

	}

	@GET
	@Path("schulen")
	public Response findSchulen() {

		this.delayService.pause();

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
	@Path("schulen/{schulkuerzel}/details")
	public Response getSchuleDetails(@PathParam(value = "schulkuerzel") final String schulkuerzel) {

		this.delayService.pause();

		Principal principal = securityContext.getUserPrincipal();

		SchuleAPIModel schule = this.schulenAnmeldeinfoService.getSchuleWithWettbewerbsdetails(schulkuerzel, principal.getName());

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), schule)).build();
		return response;
	}

	@POST
	@Path("schulen/{schulkuerzel}")
	public Response addSchule(@PathParam(value = "schulkuerzel") @Kuerzel final String schulkuerzel) {

		this.delayService.pause();

		Principal principal = securityContext.getUserPrincipal();

		final Identifier lehrerID = new Identifier(principal.getName());
		final Identifier schuleID = new Identifier(schulkuerzel);

		ResponsePayload responsePayload = lehrerService.addSchule(lehrerID, schuleID);

		SchuleAPIModel schule = this.schulenAnmeldeinfoService.getSchuleWithWettbewerbsdetails(schulkuerzel, principal.getName());

		responsePayload.setData(schule);

		return Response.ok(responsePayload).build();
	}

	@DELETE
	@Path("schulen/{schulkuerzel}")
	public Response removeSchule(@PathParam(value = "schulkuerzel") @Kuerzel final String schulkuerzel) {

		this.delayService.pause();

		Principal principal = securityContext.getUserPrincipal();

		final Identifier lehrerID = new Identifier(principal.getName());
		final Identifier schuleID = new Identifier(schulkuerzel);

		boolean kannAbmelden = this.checkRemoveSchuleService.kannLehrerVonSchuleAbmelden(lehrerID, schuleID);

		if (!kannAbmelden) {

			ResponsePayload responsePayload = ResponsePayload
				.messageOnly(MessagePayload
					.warn(applicationMessages.getString("lehrer.schulen.remove.schule_zum_wettbewerb_angemeldet.warn")));

			// Conflict
			return Response.status(409).entity(responsePayload).build();
		}

		ResponsePayload responsePayload = this.lehrerService.removeSchule(lehrerID, schuleID);

		return Response.ok(responsePayload).build();
	}

	@DELETE
	@Path("schulen/{schulkuerzel}/klassen")
	public Response removeKlassen(@PathParam(value = "schulkuerzel") @Kuerzel final String schulkuerzel) {

		this.delayService.pause();

		Principal principal = securityContext.getUserPrincipal();

		final Identifier schuleID = new Identifier(schulkuerzel);

		ResponsePayload responsePayload = this.klassenService.alleKlassenLoeschen(schuleID, principal.getName());

		return Response.ok(responsePayload).build();
	}
}
