// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.mk_gateway.domain.signup.LehrerCreated;
import de.egladil.web.mk_gateway.domain.signup.PrivatmenschCreated;

/**
 * MkWettbewerbRestClient wird in application.properties konfiguriert.
 */
@RegisterRestClient
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MkWettbewerbRestClient {

	@POST
	@Path("/personen/lehrer")
	Response createLehrer(LehrerCreated data);

	@POST
	@Path("/personen/privat")
	Response createPrivatmensch(PrivatmenschCreated data);

}
