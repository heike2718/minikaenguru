// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.EditWettbewerbModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbDetailsAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbListAPIModel;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * AdminWettbewerbResource .../mk-gateway/admin/...
 */
@RequestScoped
@Path("admin/wettbewerbe")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminWettbewerbResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminWettbewerbResource.class);

	@ConfigProperty(name = "admin.created.uri.prefix", defaultValue = "https://mathe-jung-alt.de/mk-gateway/admin")
	String createdUriPrefix;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	DevDelayService delayService;

	@GET
	public Response loadWettbewerbe() {

		this.delayService.pause();

		List<WettbewerbListAPIModel> wettbewerbe = this.wettbewerbService.alleWettbewerbeHolen();

		ResponsePayload payload = new ResponsePayload(MessagePayload.ok(), wettbewerbe);
		return Response.ok(payload).build();
	}

	@GET
	@Path("wettbewerb/{jahr}")
	public Response wettbewerbMitJahr(@PathParam(value = "jahr") final Integer jahr) {

		this.delayService.pause();

		Optional<WettbewerbDetailsAPIModel> optDaten = this.wettbewerbService.wettbewerbMitJahr(jahr);

		if (optDaten.isEmpty()) {

			return Response.status(Status.NOT_FOUND)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("kein Wettbwerb mit Jahr " + jahr + " bekannt"))).build();
		}

		return Response.ok(new ResponsePayload(MessagePayload.ok(), optDaten.get())).build();
	}

	@POST
	@Path("wettbewerb")
	public Response wettbewerbAnlegen(final EditWettbewerbModel data) {

		this.delayService.pause();

		Optional<WettbewerbDetailsAPIModel> optVorhanden = this.wettbewerbService.wettbewerbMitJahr(data.getJahr());

		if (optVorhanden.isPresent()) {

			return Response.status(409).entity(ResponsePayload.messageOnly(MessagePayload.warn("Diesen Wettbewerb gibt es schon")))
				.build();
		}

		Wettbewerb wettbewerb = this.wettbewerbService.wettbewerbAnlegen(data);

		ResponsePayload payload = ResponsePayload
			.messageOnly(MessagePayload.info("Wettbewerb " + wettbewerb.toString() + " erfolgreich angelegt"));

		String locationString = createdUriPrefix + "/wettbewerbe/wettbewerb/" + wettbewerb.id().jahr();
		URI location = createdUri(locationString);
		return Response.created(location).entity(payload).build();
	}

	@PUT
	@Path("wettbewerb")
	public Response wettbewerbAendern(final EditWettbewerbModel data) {

		this.delayService.pause();

		this.wettbewerbService.wettbewerbAendern(data);

		ResponsePayload payload = ResponsePayload
			.messageOnly(MessagePayload.info("Wettbewerb " + data.getJahr() + " erfolgreich gespeichert"));

		return Response.ok(payload).build();
	}

	@PUT
	@Path("wettbewerb/status")
	public Response starteNaechstePhase(final WettbewerbID wettbewerbId) {

		this.delayService.pause();

		WettbewerbStatus neuerStatus = wettbewerbService.starteNaechstePhase(wettbewerbId.jahr());

		ResponsePayload payload = new ResponsePayload(
			MessagePayload.info("Wettbewerb " + wettbewerbId.jahr() + " erfolgreich in nächste Phase befördert"),
			neuerStatus.toString());

		return Response.ok(payload).build();
	}

	@GET
	@Path("aktueller")
	public Response getAktuellenWettbewerb() {

		this.delayService.pause();

		Optional<Wettbewerb> optWettbewerb = this.wettbewerbService.aktuellerWettbewerb();

		if (!optWettbewerb.isPresent()) {

			throw new NotFoundException();
		}

		WettbewerbAPIModel wettbewerbAPIModel = WettbewerbAPIModel.fromWettbewerb(optWettbewerb.get());

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), wettbewerbAPIModel);

		return Response.ok(responsePayload).build();

	}

	URI createdUri(final String locationString) {

		try {

			return new URI(locationString);
		} catch (URISyntaxException e) {

			LOGGER.error("Fehlerhafte URI {}: {} ", locationString, e.getMessage(), e);
			throw new MkGatewayRuntimeException("Fehlerhafte URI: " + locationString, e);
		}
	}
}
