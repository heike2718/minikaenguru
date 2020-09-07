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
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.MkGatewayApp;
import de.egladil.web.mk_gateway.domain.apimodel.FileResource;
import de.egladil.web.mk_gateway.domain.apimodel.kataloge.LandPayload;
import de.egladil.web.mk_gateway.domain.apimodel.kataloge.OrtPayload;
import de.egladil.web.mk_gateway.domain.apimodel.kataloge.SchulePayload;
import de.egladil.web.mk_gateway.domain.apimodel.kataloge.SchulkatalogAntrag;

/**
 * MkKatalogeRestClient
 */
@RegisterRestClient
@Path("/mk-kataloge")
@Produces(MediaType.APPLICATION_JSON)
public interface MkKatalogeRestClient {

	@GET
	@Path("/katalogsuche/global/{typ}")
	public Response findItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm);

	@GET
	@Path("/katalogsuche/laender/{land}/orte")
	public Response findOrteInLand(@LandKuerzel @PathParam(
		value = "land") final String landKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm);

	@GET
	@Path("/katalogsuche/orte/{ort}/schulen")
	public Response findSchulenInOrt(@Kuerzel @PathParam(
		value = "ort") final String ortKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm);

	/**
	 * Response.entity.data hat die Signatur von List<KatalogItem>
	 *
	 * @param  kommaseparierteKuerzel
	 *                                String
	 * @return                        Response Liste von KatalogItems als data.
	 */
	@GET
	@Path("/kataloge/schulen/{kommaseparierteKuerzel}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response findSchulenMitKuerzeln(@PathParam(
		value = "kommaseparierteKuerzel") @Kuerzel final String kommaseparierteKuerzel);

	@GET
	@Path("/kataloge/laender")
	@Consumes(MediaType.APPLICATION_JSON)
	Response loadLaender(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret);

	@GET
	@Path("/kataloge/laender/{kuerzel}/orte")
	@Consumes(MediaType.APPLICATION_JSON)
	Response loadOrteInLand(@PathParam(
		value = "kuerzel") final String kuerzel);

	@GET
	@Path("/kataloge/orte/{kuerzel}/schulen")
	@Consumes(MediaType.APPLICATION_JSON)
	Response loadSchulenInOrt(@PathParam(
		value = "kuerzel") final String kuerzel);

	@PUT
	@Path("/kataloge/laender")
	@Consumes(MediaType.APPLICATION_JSON)
	Response renameLand(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, final LandPayload requestPayload);

	@PUT
	@Path("/kataloge/orte")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response renameOrt(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, final OrtPayload requestPayload);

	@PUT
	@Path("/kataloge/schulen")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response renameSchule(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, SchulePayload requestPayload);

	@POST
	@Path("/kataloge/schulen")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createSchule(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, SchulePayload requestPayload);

	@GET
	@Path("/katalogsuche/global/{typ}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response searchItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm);

	@GET
	@Path("/kuerzel")
	@Consumes(MediaType.APPLICATION_JSON)
	Response generateKuerzel(@HeaderParam(
		value = MkGatewayApp.UUID_HEADER_NAME) final String secret);

	@POST
	@Path("/upload/schulen/csv")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	Response uploadSchulkatalog(@HeaderParam(
		value = MkGatewayApp.SECRET_HEADER_NAME) final String secret, @MultipartForm final FileResource input);

	@POST
	@Path("/katalogantrag")
	@Consumes(MediaType.APPLICATION_JSON)
	Response sendeSchulkatalogAntrag(final SchulkatalogAntrag antrag);
}
