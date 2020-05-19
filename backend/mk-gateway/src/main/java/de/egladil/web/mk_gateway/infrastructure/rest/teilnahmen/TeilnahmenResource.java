// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.teilnahmen;

import java.security.Principal;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.mk_gateway.domain.wettbewerb.MkWettbewerbResourceAdapter;

/**
 * TeilnahmenResource
 */
@RequestScoped
@Path("/wettbewerb")
@Produces(MediaType.APPLICATION_JSON)
public class TeilnahmenResource {

	@Inject
	MkWettbewerbResourceAdapter mkWettbewerbResourceAdapter;

	@Context
	SecurityContext securityContext;

	@GET
	@Path("/teilnahmen/teilnahmenummern")
	public Response getOwnTeilnahmenummern() {

		Principal principal = securityContext.getUserPrincipal();

		Response response = mkWettbewerbResourceAdapter.getTeilnahmenummern(principal.getName());
		return response;
	}

	@GET
	@Path("/schulen/{schulkuerzel}/details")
	public Response getSchuleDetails(@PathParam(value = "schulkuerzel") final String schulkuerzel) {

		Principal principal = securityContext.getUserPrincipal();

		Response response = mkWettbewerbResourceAdapter.getSchuleDashboardModel(schulkuerzel, principal.getName());
		return response;

	}
}
