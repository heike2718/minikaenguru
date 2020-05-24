// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.infrastructure.rest;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb_admin.MkWettbewerbAdminApp;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.WettbewerbDetailsAPIModel;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.WettbewerbListAPIModel;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.WettbewerbService;

/**
 * WettbewerbeResource
 */
@Path("/wettbewerbe")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WettbewerbeResource extends AbstractAdminResource {

	@Inject
	WettbewerbService wettbewerbService;

	@GET
	public Response loadAllWettbewerbe(@HeaderParam(
		value = MkWettbewerbAdminApp.UUID_HEADER_NAME) final String principalName) {

		this.checkAccess(principalName);

		List<WettbewerbListAPIModel> wettbewerbe = this.wettbewerbService.alleWettbewerbeHolen();

		ResponsePayload payload = new ResponsePayload(MessagePayload.ok(), wettbewerbe);
		return Response.ok(payload).build();
	}

	@GET
	@Path("/wettbewerb/{jahr}")
	public Response wettbewerbMitJahr(@PathParam(value = "jahr") final Integer jahr, @HeaderParam(
		value = MkWettbewerbAdminApp.UUID_HEADER_NAME) final String principalName) {

		this.checkAccess(principalName);

		Optional<WettbewerbDetailsAPIModel> optDaten = this.wettbewerbService.wettbewerbMitJahr(jahr);

		if (optDaten.isEmpty()) {

			return Response.status(Status.NOT_FOUND)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("kein Wettbwerb mit Jahr " + jahr + " bekannt"))).build();
		}

		return Response.ok(new ResponsePayload(MessagePayload.ok(), optDaten.get())).build();
	}

}
