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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.domain.apimodel.LandPayload;
import de.egladil.web.mk_gateway.domain.apimodel.OrtPayload;
import de.egladil.web.mk_gateway.domain.apimodel.SchulePayload;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;

/**
 * KatalogAdminResource
 */
@RequestScoped
@Path("/wb-admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KatalogAdminResource {

	@Context
	SecurityContext securityContext;

	@ConfigProperty(name = "admin.secret")
	String katalogAdminSecret;

	@Inject
	MkKatalogeResourceAdapter katalogResourceAdapter;

	@GET
	@Path("/kataloge/laender")
	public Response loadLaender() {

		return katalogResourceAdapter.loadLaender(securityContext.getUserPrincipal().getName(), katalogAdminSecret);
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

	@PUT
	@Path("/kataloge/laender")
	public Response renameLand(final LandPayload requestPayload) {

		return null;
	}

	@PUT
	@Path("/kataloge/orte")
	public Response renameOrt(final OrtPayload requestPayload) {

		return null;
	}

	@PUT
	@Path("/kataloge/schulen")
	public Response renameSchule(final SchulePayload requestPayload) {

		return null;
	}

	@POST
	@Path("/kataloge/schulen")
	public Response createSchule(final SchulePayload requestPayload) {

		String uuid = securityContext.getUserPrincipal().getName();
		return katalogResourceAdapter.createSchule(uuid, katalogAdminSecret, requestPayload);

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
