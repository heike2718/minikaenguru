// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.domain.apimodel.NeueSchulePayload;
import de.egladil.web.mk_gateway.domain.apimodel.RenameKatalogItemPayload;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;

/**
 * KatalogAdminResource
 */
@RequestScoped
@Path("/wb-admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KatalogAdminResource {

	@ConfigProperty(name = "admin.secret")
	String katalogAdminSecret;

	@Inject
	MkKatalogeResourceAdapter katalogResourceAdapter;

	@GET
	@Path("/kataloge/laender")
	public Response loadLaender() {

		return katalogResourceAdapter.loadLaender(katalogAdminSecret);
	}

	@GET
	@Path("/kataloge/laender/{kuerzel}/orte")
	public Response loadOrteInLand(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		return katalogResourceAdapter.loadOrteInLand(kuerzel);
	}

	@GET
	@Path("/kataloge/orte/{kuerzel}/schulen")
	public Response loadSchulenInOrt(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		return katalogResourceAdapter.loadSchulenInOrt(kuerzel);
	}

	@POST
	@Path("/kataloge/laender/{kuerzel}")
	public Response renameLand(@PathParam(
		value = "kuerzel") @NotBlank @Kuerzel final String kuerzel, final RenameKatalogItemPayload requestPayload) {

		return null;
	}

	@POST
	@Path("/kataloge/orte/{kuerzel}")
	public Response renameOrt(@PathParam(
		value = "kuerzel") @NotBlank @Kuerzel final String kuerzel, final RenameKatalogItemPayload requestPayload) {

		return null;
	}

	@POST
	@Path("/kataloge/schulen/{kuerzel}")
	public Response renameSchule(@PathParam(
		value = "kuerzel") @NotBlank @Kuerzel final String kuerzel, final RenameKatalogItemPayload requestPayload) {

		return null;
	}

	@PUT
	@Path("/kataloge/schulen")
	public Response createSchule(final NeueSchulePayload requestPayload) {

		return null;

	}

	@GET
	@Path("/katalogsuche/global/{typ}")
	public Response sucheItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		return katalogResourceAdapter.sucheItems(typ, searchTerm);
	}

	@GET
	@Path("/kuerzel")
	public Response generateKuerzel() {

		return katalogResourceAdapter.generateKuerzel(katalogAdminSecret);
	}
}
