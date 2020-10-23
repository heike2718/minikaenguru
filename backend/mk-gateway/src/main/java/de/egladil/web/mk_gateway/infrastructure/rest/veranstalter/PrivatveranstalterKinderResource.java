// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.mk_gateway.domain.kinder.PrivatkinderService;
import de.egladil.web.mk_gateway.domain.kinder.api.PrivatkindRequestData;

/**
 * PrivatveranstalterKinderResource
 */
@RequestScoped
@Path("/privatkinder")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PrivatveranstalterKinderResource {

	@Inject
	PrivatkinderService privatkinderService;

	@Context
	SecurityContext securityContext;

	@POST
	public Response createKind(final PrivatkindRequestData data) {

		return null;
	}
}
