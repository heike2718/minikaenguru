// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.security.Principal;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.LehrerAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.veranstalter.LehrerService;
import de.egladil.web.mk_gateway.domain.veranstalter.SchulenAnmeldeinfoService;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterAuthorizationService;

/**
 * VeranstalterLehrerResource
 */
@RequestScoped
@Path("/veranstalter/lehrer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VeranstalterLehrerResource {

	private static final Logger LOG = LoggerFactory.getLogger(VeranstalterLehrerResource.class);

	@Context
	SecurityContext securityContext;

	@Inject
	SchulenAnmeldeinfoService schulenAnmeldeinfoService;

	@Inject
	VeranstalterAuthorizationService veranstalterAuthService;

	@Inject
	LehrerService lehrerService;

	static VeranstalterLehrerResource createForPermissionTest(final VeranstalterAuthorizationService veranstalterAuthService, final LehrerService lehrerService, final SecurityContext securityContext) {

		VeranstalterLehrerResource result = new VeranstalterLehrerResource();
		result.veranstalterAuthService = veranstalterAuthService;
		result.lehrerService = lehrerService;
		result.securityContext = securityContext;
		return result;
	}

	@GET
	public Response getLehrer() {

		Principal principal = securityContext.getUserPrincipal();

		LehrerAPIModel lehrer = lehrerService.findLehrer(principal.getName());
		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), lehrer);

		return Response.ok(responsePayload).build();

	}

	@GET
	@Path("/schulen")
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
	@Path("/schulen/{schulkuerzel}/details")
	public Response getSchuleDetails(@PathParam(value = "schulkuerzel") final String schulkuerzel) {

		Principal principal = securityContext.getUserPrincipal();

		final Identifier lehrerID = new Identifier(principal.getName());
		final Identifier schuleID = new Identifier(schulkuerzel);

		veranstalterAuthService.checkPermissionForTeilnahmenummer(lehrerID, schuleID);

		SchuleAPIModel schule = this.schulenAnmeldeinfoService.getSchuleWithWettbewerbsdetails(schulkuerzel, principal.getName());

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), schule)).build();
		return response;
	}
}
