// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * AdminStatistikResource
 */
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/admin/statistik")
public class AdminStatistikResource {

	// TODO: path und auth
	public Response getStatistikFuerSchule() {

		return null;
	}

	// TODO: path und auth
	public Response getStatistikFuerOrt() {

		return null;
	}

	// TODO: path und auth
	public Response getStatistikFuerLand() {

		return null;
	}

}
