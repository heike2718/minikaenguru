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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.kataloge.api.LandPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.OrtPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;

/**
 * AdminKatalogResource
 */
@RequestScoped
@Path("/admin/kataloge")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminKatalogResource {

	private static final Logger LOG = LoggerFactory.getLogger(AdminKatalogResource.class);

	@Context
	SecurityContext securityContext;

	@ConfigProperty(name = "admin.secret")
	String katalogAdminSecret;

	@Inject
	MkKatalogeResourceAdapter katalogResourceAdapter;

	@GET
	@Path("/laender")
	public Response loadLaender() {

		return katalogResourceAdapter.loadLaender(securityContext.getUserPrincipal().getName(), katalogAdminSecret);
	}

	@GET
	@Path("/laender/{kuerzel}/orte")
	public Response loadOrteInLand(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		return katalogResourceAdapter.loadOrteInLand(kuerzel);
	}

	@GET
	@Path("/orte/{kuerzel}/schulen")
	public Response loadSchulenInOrt(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		return katalogResourceAdapter.loadSchulenInOrt(kuerzel);
	}

	@PUT
	@Path("/laender")
	public Response renameLand(final LandPayload requestPayload) {

		String uuid = securityContext.getUserPrincipal().getName();
		return katalogResourceAdapter.renameLand(uuid, katalogAdminSecret, requestPayload);
	}

	@PUT
	@Path("/orte")
	public Response renameOrt(final OrtPayload requestPayload) {

		String uuid = securityContext.getUserPrincipal().getName();

		LOG.debug("Start: uuid={}, {}", uuid, requestPayload);
		return katalogResourceAdapter.renameOrt(uuid, katalogAdminSecret, requestPayload);
	}

	@PUT
	@Path("/schulen")
	public Response renameSchule(final SchulePayload requestPayload) {

		String uuid = securityContext.getUserPrincipal().getName();
		return katalogResourceAdapter.renameSchule(uuid, katalogAdminSecret, requestPayload);
	}

	@POST
	@Path("/schulen")
	public Response createSchule(final SchulePayload requestPayload) {

		String uuid = securityContext.getUserPrincipal().getName();
		return katalogResourceAdapter.createSchule(uuid, katalogAdminSecret, requestPayload);

	}

	@GET
	@Path("/suche/global/{typ}")
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
