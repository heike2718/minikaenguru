// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.infrastructure.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb_admin.MkWettbewerbAdminApp;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.EditWettbewerbModel;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.WettbewerbDetailsAPIModel;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.WettbewerbListAPIModel;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.WettbewerbStatus;

/**
 * WettbewerbeResource
 */
@Path("/wettbewerbe")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WettbewerbeResource extends AbstractAdminResource {

	@Inject
	WettbewerbService wettbewerbService;

	@ConfigProperty(name = "created.uri.prefix", defaultValue = "https://mathe-jung-alt.de/mk-gateway/wb-admin")
	String createdUriPrefix;

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

	@POST
	@Path("/wettbewerb")
	public Response wettbewerbAnlegen(final EditWettbewerbModel data, @HeaderParam(
		value = MkWettbewerbAdminApp.UUID_HEADER_NAME) final String principalName) {

		this.checkAccess(principalName);

		Optional<WettbewerbDetailsAPIModel> optVorhanden = this.wettbewerbService.wettbewerbMitJahr(data.getJahr());

		if (optVorhanden.isPresent()) {

			return Response.status(409).entity(ResponsePayload.messageOnly(MessagePayload.warn("Diesen Wettbewerb gibt es schon")))
				.build();
		}

		Wettbewerb wettbewerb = this.wettbewerbService.wettbewerbAnlegen(data);

		ResponsePayload payload = ResponsePayload
			.messageOnly(MessagePayload.info("Wettbewerb " + wettbewerb.toString() + " erfolgreich gespeichert"));

		String locationString = createdUriPrefix + "/wettbewerbe/wettbewerb/" + wettbewerb.id().jahr();
		URI location = createdUri(locationString);
		return Response.created(location).entity(payload).build();
	}

	@PUT
	@Path("/wettbewerb/status")
	public Response starteNaechstePhase(final WettbewerbID wettbewerbId, @HeaderParam(
		value = MkWettbewerbAdminApp.UUID_HEADER_NAME) final String principalName) {

		this.checkAccess(principalName);

		WettbewerbStatus neuerStatus = wettbewerbService.starteNaechstePhase(wettbewerbId.jahr());

		ResponsePayload payload = new ResponsePayload(
			MessagePayload.info("Wettbewerb " + wettbewerbId.jahr() + " erfolgreich in nächste Phase befördert"),
			neuerStatus.toString());

		return Response.ok(payload).build();
	}
}
