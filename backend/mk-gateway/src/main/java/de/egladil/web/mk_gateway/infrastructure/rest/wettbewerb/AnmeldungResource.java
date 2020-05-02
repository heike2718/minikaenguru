// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.wettbewerb;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.session.LoggedInUser;

/**
 * AnmeldungResource
 */
@RequestScoped
@Path("/wettbewerb/anmeldungen")
@Produces(MediaType.APPLICATION_JSON)
public class AnmeldungResource {

	private static final Logger LOG = LoggerFactory.getLogger(AnmeldungResource.class);

	@Context
	SecurityContext securityContext;

	@POST
	@Path("/anmeldung")
	public Response anmelden() {

		LoggedInUser loggedInUser = (LoggedInUser) securityContext.getUserPrincipal();

		LOG.info("Hier jetzt routen zu mk-wettbewerbe mit der uuid und der Rolle des Users {}", loggedInUser);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.warn("Diese Methode ist noch nicht fertig"))).build();
	}

}
