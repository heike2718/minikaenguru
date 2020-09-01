// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.mk_gateway.domain.wettbewerb.MkWettbewerbResourceAdapter;

/**
 * PublicWettbewerbResource
 */
@RequestScoped
@Path("/wettbewerb")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PublicWettbewerbResource {

	@Inject
	MkWettbewerbResourceAdapter mkWettbewerbResourceAdapter;

	@GET
	@Path("/aktueller")
	public Response getAktuellenWettbewerb() {

		return mkWettbewerbResourceAdapter.getAktuellenWettbewerb();
	}

}
