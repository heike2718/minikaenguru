// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.kataloge.api.LandPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.OrtPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulkatalogAntrag;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * MkKatalogeRestClient
 */
@RegisterRestClient
@Path("mk-kataloge")
@Produces(MediaType.APPLICATION_JSON)
public interface MkKatalogeRestClient {

	@GET
	@Path("heartbeats")
	public Response checkKataloge(@HeaderParam("X-HEARTBEAT-ID") final String heartbeatId);

	@GET
	@Path("katalogsuche/global/{typ}")
	public Response findItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm);

	@GET
	@Path("katalogsuche/laender/{land}/orte")
	public Response findOrteInLand(@LandKuerzel @PathParam(
		value = "land") final String landKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm);

	@GET
	@Path("katalogsuche/orte/{ort}/schulen")
	public Response findSchulenInOrt(@Kuerzel @PathParam(
		value = "ort") final String ortKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm);

	/**
	 * Response.entity.data hat die Signatur von List von SchuleAPIModel-Objekten
	 *
	 * @param  kommaseparierteKuerzel
	 *                                String
	 * @return                        Response Liste von SchuleAPIModel als data.
	 */
	@GET
	@Path("schulinfos/{kommaseparierteKuerzel}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response findSchulenMitKuerzeln(@PathParam(
		value = "kommaseparierteKuerzel") @Kuerzel final String kommaseparierteKuerzel);

	/**
	 * Response.entity ist eine List von SchuleAPIModel
	 *
	 * @param  schulkuerzel
	 *                      StringsAPIModel
	 * @return
	 */
	@POST
	@Path("schulinfos")
	@Consumes(MediaType.TEXT_PLAIN)
	Response loadSchulenMitKuerzeln(final String schulkuerzel);

	@GET
	@Path("kataloge/laender")
	@Consumes(MediaType.APPLICATION_JSON)
	Response loadLaender(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret);

	@GET
	@Path("kataloge/laender/{kuerzel}/orte")
	@Consumes(MediaType.APPLICATION_JSON)
	Response loadOrteInLand(@PathParam(
		value = "kuerzel") final String kuerzel);

	@GET
	@Path("kataloge/orte/{kuerzel}/schulen")
	@Consumes(MediaType.APPLICATION_JSON)
	Response loadSchulenInOrt(@PathParam(
		value = "kuerzel") final String kuerzel);

	@PUT
	@Path("kataloge/laender")
	@Consumes(MediaType.APPLICATION_JSON)
	Response renameLand(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, final LandPayload requestPayload);

	@PUT
	@Path("kataloge/orte")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response renameOrt(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, final OrtPayload requestPayload);

	@PUT
	@Path("kataloge/schulen")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response renameSchule(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, SchulePayload requestPayload);

	@POST
	@Path("kataloge/schulen")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createSchule(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, SchulePayload requestPayload);

	@GET
	@Path("katalogsuche/global/{typ}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response searchItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm);

	@GET
	@Path("kuerzel")
	@Consumes(MediaType.APPLICATION_JSON)
	Response generateKuerzel(@HeaderParam(
		value = MkGatewayApp.SECRET_HEADER_NAME) final String secret);

	@POST
	@Path("katalogantrag")
	@Consumes(MediaType.APPLICATION_JSON)
	Response sendeSchulkatalogAntrag(final SchulkatalogAntrag antrag);

}
