// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.wettbewerb;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.SchulanmeldungRequestPayload;
import de.egladil.web.mk_gateway.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.permissions.RestrictedUrlPath;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.MkWettbewerbResourceAdapter;
import de.egladil.web.mk_gateway.domain.wettbewerb.SchulenAnmeldeinfoService;

/**
 * WettbewerbResource
 */
@RequestScoped
@Path("/wettbewerb")
@Produces(MediaType.APPLICATION_JSON)
public class WettbewerbResource {

	private static final Logger LOG = LoggerFactory.getLogger(WettbewerbResource.class);

	@Inject
	MkWettbewerbResourceAdapter mkWettbewerbResourceAdapter;

	@Inject
	SchulenAnmeldeinfoService schulenAnmeldeinfoService;

	@Context
	SecurityContext securityContext;

	@GET
	@Path("/lehrer/schulen")
	public Response findSchulen() {

		Principal principal = securityContext.getUserPrincipal();

		try {

			List<SchuleAPIModel> schulen = schulenAnmeldeinfoService
				.findSchulenMitAnmeldeinfo(principal.getName());

			ResponsePayload payload = new ResponsePayload(MessagePayload.ok(), schulen);

			return Response.ok(payload).build();
		} catch (Exception e) {

			LOG.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException(e.getMessage());
		}
	}

	@GET
	@Path("/lehrer/schulen/{schulkuerzel}/details")
	public Response getSchuleDetails(@PathParam(value = "schulkuerzel") final String schulkuerzel) {

		Principal principal = securityContext.getUserPrincipal();

		SchuleAPIModel schule = this.schulenAnmeldeinfoService.getSchuleWithWettbewerbsdetails(schulkuerzel, principal.getName());

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), schule)).build();
		return response;
	}

	@POST
	@Path("/teilnahmen/privat")
	public Response meldePrivatmenschZumAktuellenWettbewerbAn() {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.meldePrivatmenschZumAktuellenWettbewerbAn(principal.getName());
	}

	@POST
	@Path("/teilnahmen/schule")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meldeSchuleZumAktuellenWettbewerbAn(final SchulanmeldungRequestPayload payload) {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.meldeSchuleZumAktuellenWettbewerbAn(payload, principal.getName());
	}

	@GET
	@Path("/aktueller")
	public Response getAktuellenWettbewerb() {

		return mkWettbewerbResourceAdapter.getAktuellenWettbewerb();
	}

	@GET
	@Path("/veranstalter/zugangsstatus")
	public Response getStatusZugangUnterlagen() {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.getStatusZugangUnterlagen(principal.getName());

	}

	@GET
	@Path("/veranstalter/lehrer")
	public Response getLehrer() {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.getStatusZugangUnterlagen(principal.getName());

	}

	@GET
	@Path("/veranstalter/privat")
	public Response getPrivatveranstalter() {

		Principal principal = securityContext.getUserPrincipal();

		return mkWettbewerbResourceAdapter.getPrivatveranstalter(principal.getName());

	}

	public static List<RestrictedUrlPath> getRestrictedPathInfos() {

		List<RestrictedUrlPath> paths = new ArrayList<>();

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/teilnahmen/privat",
				Arrays.asList(new Rolle[] { Rolle.PRIVAT }), Arrays.asList(new String[] { HttpMethod.POST }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/teilnahmen/schule",
				Arrays.asList(new Rolle[] { Rolle.LEHRER }), Arrays.asList(new String[] { HttpMethod.POST }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/teilnahmen/schulen/*",
				Arrays.asList(new Rolle[] { Rolle.LEHRER }), Arrays.asList(new String[] { HttpMethod.GET }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/lehrer/schulen",
				Arrays.asList(new Rolle[] { Rolle.LEHRER }), Arrays.asList(new String[] { HttpMethod.GET }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/lehrer/schulen/*/details",
				Arrays.asList(new Rolle[] { Rolle.LEHRER }), Arrays.asList(new String[] { HttpMethod.GET }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/veranstalter/zugangsstatus",
				Arrays.asList(new Rolle[] { Rolle.LEHRER, Rolle.PRIVAT }), Arrays.asList(new String[] { HttpMethod.GET }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/veranstalter/privat",
				Arrays.asList(new Rolle[] { Rolle.PRIVAT }), Arrays.asList(new String[] { HttpMethod.GET }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/veranstalter/lehrer",
				Arrays.asList(new Rolle[] { Rolle.LEHRER }), Arrays.asList(new String[] { HttpMethod.GET, HttpMethod.PUT }));
			paths.add(restrictedPath);
		}

		return paths;
	}
}
