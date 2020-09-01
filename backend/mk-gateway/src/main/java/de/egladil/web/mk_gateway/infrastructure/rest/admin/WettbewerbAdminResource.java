// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.egladil.web.mk_gateway.domain.permissions.PathWithMethod;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * WettbewerbAdminResource .../mk-gateway/admin/...
 */
@RequestScoped
@Path("/admin/wettbewerbe")
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

	public static Map<PathWithMethod, List<Rolle>> getPathWithMethod2Rollen() {

		Map<PathWithMethod, List<Rolle>> result = new HashMap<>();

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/wettbewerbe", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/wettbewerbe/wettbewerb", HttpMethod.POST), rollen);
			result.put(new PathWithMethod("/admin/wettbewerbe/wettbewerb", HttpMethod.PUT), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/wettbewerbe/wettbewerb/status", HttpMethod.PUT), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/wettbewerbe/wettbewerb/*", HttpMethod.GET), rollen);

		}

		return result;

	}

}
