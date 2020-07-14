// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.apimodel.LandPayload;
import de.egladil.web.mk_gateway.domain.apimodel.OrtPayload;
import de.egladil.web.mk_gateway.domain.apimodel.SchulePayload;

/**
 * MkKatalogeRestClient
 */
@RegisterRestClient
@Path("/mk-kataloge-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MkKatalogeRestClient {

	/**
	 * Response.entity.data hat die Signatur von List<KatalogItem>
	 *
	 * @param  kommaseparierteKuerzel
	 *                                String
	 * @return                        Response Liste von KatalogItems als data.
	 */
	@GET
	@Path("/kataloge/schulen/{kommaseparierteKuerzel}")
	Response findSchulenMitKuerzeln(@PathParam(
		value = "kommaseparierteKuerzel") @Kuerzel final String kommaseparierteKuerzel) throws MkKatalogeRestException;

	@GET
	@Path("/kataloge/laender")
	Response loadLaender(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret);

	@GET
	@Path("/kataloge/laender/{kuerzel}/orte")
	Response loadOrteInLand(@PathParam(
		value = "kuerzel") final String kuerzel);

	@GET
	@Path("/kataloge/orte/{kuerzel}/schulen")
	Response loadSchulenInOrt(@PathParam(
		value = "kuerzel") final String kuerzel);

	@PUT
	@Path("/kataloge/laender")
	Response renameLand(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, final LandPayload requestPayload);

	@PUT
	@Path("/kataloge/orte")
	public Response renameOrt(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, final OrtPayload requestPayload);

	@PUT
	@Path("/kataloge/schulen")
	public Response renameSchule(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, SchulePayload requestPayload);

	@POST
	@Path("/kataloge/schulen")
	public Response createSchule(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, SchulePayload requestPayload);

	@GET
	@Path("/katalogsuche/global/{typ}")
	Response searchItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm);

	@GET
	@Path("/kuerzel")
	Response generateKuerzel(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String secret);

}
