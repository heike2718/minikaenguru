// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.mk_gateway.domain.admin.MkWettbewerbAdminResourceAdapter;
import de.egladil.web.mk_gateway.domain.error.AuthException;

/**
 * WettbewerbAdminResource .../mk-gateway/wb-admin/...
 */
@RequestScoped
@Path("/wb-admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WettbewerbAdminResource {

	@Context
	SecurityContext securityContext;

	@Inject
	MkWettbewerbAdminResourceAdapter resourceAdapter;

	@GET
	@Path("/wettbewerbe")
	public Response loadWettbewerbe() {

		if (securityContext.getUserPrincipal() == null) {

			throw new AuthException("nicht eingeloggt oder keine gültige session mehr");
		}

		String principalName = securityContext.getUserPrincipal().getName();

		return resourceAdapter.loadWettbewerbe(principalName);
	}

}
