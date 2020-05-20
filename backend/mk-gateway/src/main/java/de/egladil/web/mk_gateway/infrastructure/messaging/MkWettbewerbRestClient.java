// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.signup.LehrerCreated;
import de.egladil.web.mk_gateway.domain.signup.PrivatmenschCreated;

/**
 * MkWettbewerbRestClient wird in application.properties konfiguriert.
 */
@RegisterRestClient
@Path("/mk-wettbewerb")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MkWettbewerbRestClient {

	// http://192.168.10.176:9550/mk-wettbewerb/personen/lehrer
	@POST
	@Path("/personen/lehrer")
	Response createLehrer(LehrerCreated data) throws MkWettbewerbRestException;

	// http://192.168.10.176:9550/mk-wettbewerb/personen/privat
	@POST
	@Path("/personen/privat")
	Response createPrivatmensch(PrivatmenschCreated data) throws MkWettbewerbRestException;

	// http://192.168.10.176:9550/mk-wettbewerb/veranstalter/teilnahmenummern, X_UUID=2f09da36-07c6-4033-a2f1-5e110c804026
	@GET
	@Path("/veranstalter/teilnahmenummern")
	Response getTeilnahmenummern(@HeaderParam(value = MkGatewayApp.UUID_HEADER_NAME) String uuid) throws MkWettbewerbRestException;

	@GET
	@Path("/teilnahmen/angemeldet/{teilnahmenummer}")
	Response getAngemeldet(@PathParam(value = "teilnahmenummer") String teilnahmenummer) throws MkWettbewerbRestException;

	@GET
	@Path("/schulen/details/{schulkuerzel}")
	Response getSchuleDetails(@PathParam(value = "schulkuerzel") final String schulkuerzel, @HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String principalName) throws MkWettbewerbRestException;

	@GET
	@Path("/schulen")
	Response findSchulen(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String principalName) throws MkWettbewerbRestException;
}
