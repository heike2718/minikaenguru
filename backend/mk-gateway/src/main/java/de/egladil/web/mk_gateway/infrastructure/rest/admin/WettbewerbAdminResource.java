// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.mk_gateway.domain.admin.MkWettbewerbAdminResourceAdapter;
import de.egladil.web.mk_gateway.domain.apimodel.WettbewerbAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.WettbewerbID;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.permissions.RestrictedUrlPath;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * WettbewerbAdminResource .../mk-gateway/wb-admin/...
 */
@RequestScoped
@Path("/wb-admin/wettbewerbe")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WettbewerbAdminResource {

	@Context
	SecurityContext securityContext;

	@Inject
	MkWettbewerbAdminResourceAdapter resourceAdapter;

	@GET
	public Response loadWettbewerbe() {

		if (securityContext.getUserPrincipal() == null) {

			throw new AuthException("nicht eingeloggt oder keine gültige session mehr");
		}

		String principalName = securityContext.getUserPrincipal().getName();

		return resourceAdapter.loadWettbewerbe(principalName);
	}

	@GET
	@Path("/wettbewerb/{jahr}")
	public Response wettbewerbMitJahr(@PathParam(value = "jahr") final Integer jahr) {

		if (securityContext.getUserPrincipal() == null) {

			throw new AuthException("nicht eingeloggt oder keine gültige session mehr");
		}

		String principalName = securityContext.getUserPrincipal().getName();

		return resourceAdapter.wettbewerbMitJahr(jahr, principalName);
	}

	@POST
	@Path("/wettbewerb")
	public Response createWettbewerb(final WettbewerbAPIModel data) {

		if (securityContext.getUserPrincipal() == null) {

			throw new AuthException("nicht eingeloggt oder keine gültige session mehr");
		}

		String principalName = securityContext.getUserPrincipal().getName();

		return resourceAdapter.createWettbewerb(data, principalName);
	}

	@PUT
	@Path("/wettbewerb")
	public Response updateWettbewerb(final WettbewerbAPIModel data) {

		if (securityContext.getUserPrincipal() == null) {

			throw new AuthException("nicht eingeloggt oder keine gültige session mehr");
		}

		String principalName = securityContext.getUserPrincipal().getName();

		return resourceAdapter.updateWettbewerb(data, principalName);
	}

	@PUT
	@Path("/wettbewerb/status")
	public Response moveWettbewerbOn(final WettbewerbID data) {

		if (securityContext.getUserPrincipal() == null) {

			throw new AuthException("nicht eingeloggt oder keine gültige session mehr");
		}

		String principalName = securityContext.getUserPrincipal().getName();

		return resourceAdapter.moveWettbwerbOn(data, principalName);
	}

	public static List<RestrictedUrlPath> getRestrictedPathInfos() {

		List<RestrictedUrlPath> paths = new ArrayList<>();

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wb-admin/wettbewerbe",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }), Arrays.asList(new String[] { HttpMethod.GET }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wb-admin/wettbewerbe/wettbewerb",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }), Arrays.asList(new String[] { HttpMethod.POST, HttpMethod.PUT }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wb-admin/wettbewerbe/wettbewerb/status",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }), Arrays.asList(new String[] { HttpMethod.PUT }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wb-admin/wettbewerbe/wettbewerb/*",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }), Arrays.asList(new String[] { HttpMethod.GET }));
			paths.add(restrictedPath);
		}
		return paths;

	}

}
