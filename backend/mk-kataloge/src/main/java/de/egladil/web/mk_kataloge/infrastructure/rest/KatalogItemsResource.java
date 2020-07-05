// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.KatalogAPIApp;
import de.egladil.web.mk_kataloge.application.KatalogFacade;
import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.apimodel.NeueSchulePayload;
import de.egladil.web.mk_kataloge.domain.apimodel.RenameKatalogItemPayload;
import de.egladil.web.mk_kataloge.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.domain.event.SecurityIncidentRegistered;

/**
 * KatalogItemsResource
 */
@ApplicationScoped
@Path("/kataloge")
@Produces(MediaType.APPLICATION_JSON)
public class KatalogItemsResource {

	private static final Logger LOG = LoggerFactory.getLogger(KatalogItemsResource.class);

	@ConfigProperty(name = "admin.secret")
	String expectedSecret;

	@ConfigProperty(name = "maximaleAnzahlTreffer", defaultValue = "25")
	int maximaleAnzahlTreffer;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	@Inject
	KatalogFacade katalogFacade;

	@GET
	@Path("/laender")
	public Response loadLaender(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String secret) {

		if (!expectedSecret.equals(secret)) {

			String msg = "Unautorisierter Versuch, die Länder zu laden: " + secret;

			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);

			return Response.status(Status.FORBIDDEN)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("Netter Versuch, aber leider keine Berechtigung")))
				.build();

		}

		List<KatalogItem> result = katalogFacade.loadLaender();

		return Response.ok(new ResponsePayload(MessagePayload.ok(), result)).build();
	}

	@POST
	@Path("/laender/{kuerzel}")
	public Response renameLand(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String secret, @PathParam(
			value = "kuerzel") @NotBlank @Kuerzel final String kuerzel, final RenameKatalogItemPayload requestPayload) {

		return null;
	}

	@POST
	@Path("/orte/{kuerzel}")
	public Response renameOrt(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String secret, @PathParam(
			value = "kuerzel") @NotBlank @Kuerzel final String kuerzel, final RenameKatalogItemPayload requestPayload) {

		return null;
	}

	@POST
	@Path("/schulen/{kuerzel}")
	public Response renameSchule(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String secret, @PathParam(
			value = "kuerzel") @NotBlank @Kuerzel final String kuerzel, final RenameKatalogItemPayload requestPayload) {

		return null;
	}

	@PUT
	@Path("/schulen")
	public Response createSchule(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String secret, final NeueSchulePayload requestPayload) {

		return null;

	}

	@GET
	@Path("/laender/{kuerzel}/orte")
	public Response loadOrteInLand(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		int anzahlTreffer = katalogFacade.countOrteInLand(kuerzel);

		if (anzahlTreffer > maximaleAnzahlTreffer) {

			ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload
				.warn("Die Abfrage ergibt zu viele Treffer. Bitte verwenden Sie die Suche /mk-kataloge-api/katalogsuche/laender/"
					+ kuerzel + "/orte?search="));

			return Response.status(Status.BAD_REQUEST).entity(responsePayload).build();
		}

		List<KatalogItem> trefferliste = katalogFacade.loadOrteInLand(kuerzel);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), trefferliste);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("/orte/{kuerzel}/schulen")
	public Response loadSchulenInOrt(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		int anzahlTreffer = katalogFacade.countSchulenInOrt(kuerzel);

		if (anzahlTreffer > maximaleAnzahlTreffer) {

			ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload
				.warn("Die Abfrage ergibt zu viele Treffer. Bitte verwenden Sie die Suche /mk-kataloge-api/katalogsuche/orte/"
					+ kuerzel + "/schulen?search="));

			return Response.status(Status.BAD_REQUEST).entity(responsePayload).build();
		}

		List<KatalogItem> trefferliste = katalogFacade.loadSchulenInOrt(kuerzel);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), trefferliste);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("/schulen/{kommaseparierteKuerzel}")
	public Response findSchulenMitKuerzeln(@PathParam(
		value = "kommaseparierteKuerzel") @Kuerzel final String kommaseparierteKuerzel) {

		List<SchuleAPIModel> trefferliste = katalogFacade.findSchulen(kommaseparierteKuerzel);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), trefferliste);

		return Response.ok(responsePayload).build();
	}
}
