// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
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
import de.egladil.web.mk_kataloge.domain.apimodel.LandPayload;
import de.egladil.web.mk_kataloge.domain.apimodel.OrtPayload;
import de.egladil.web.mk_kataloge.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
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
		value = KatalogAPIApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret) {

		if (!expectedSecret.equals(secret)) {

			String msg = "Unautorisierter Versuch, die Länder zu laden: angemeldeter ADMIN=" + adminUuid + ", secret=" + secret;

			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);

			return Response.status(Status.FORBIDDEN)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("Netter Versuch, aber leider keine Berechtigung")))
				.build();

		}

		List<KatalogItem> result = katalogFacade.loadLaender();

		return Response.ok(new ResponsePayload(MessagePayload.ok(), result)).build();
	}

	/**
	 * Ändert den Namen des gegebenen Landes und gibt das geänderte LandPayload zurück. Bei einem konkurrierenden Update wird
	 * das aktuell neueste Item zurückgegeben, damit es sofort im Editor angezeigt werden kann.
	 *
	 * @param  adminUuid
	 *                        String: die UUID des angemeldeten ADMINs
	 * @param  secret
	 *                        String teilen sich mk-gateway und mk-kataloge, da mk-kataloge keine Benutzerverwaltung hat.
	 * @param  requestPayload
	 *                        LandPayload
	 * @return                Response mit LandPayload
	 */
	@POST
	@Path("/laender")
	public Response renameLand(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret, final LandPayload requestPayload) {

		return null;
	}

	/**
	 * Benennt den Ort im gegebenen Land um und gibt das geänderte OrtPayload zurück. Bei einem konkurrierenden Update wird
	 * das aktuell neueste Item zurückgegeben, damit es sofort im Editor angezeigt werden kann.
	 *
	 * @param  adminUuid
	 *                        String: die UUID des angemeldeten ADMINs
	 * @param  secret
	 *                        String teilen sich mk-gateway und mk-kataloge, da mk-kataloge keine Benutzerverwaltung hat.
	 * @param  requestPayload
	 *                        OrtPayload
	 * @return                Response mit OrtPayload
	 */
	@POST
	@Path("/orte")
	public Response renameOrt(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret, final OrtPayload requestPayload) {

		return null;
	}

	/**
	 * Benennt die gegebene Schule um und gibt sie als SchulePayload zurück. Bei einem konkurrierenden Update wird
	 * das aktuell neueste Item zurückgegeben, damit es sofort im Editor angezeigt werden kann.
	 *
	 * @param  adminUuid
	 *                        String: die UUID des angemeldeten ADMINs
	 * @param  secret
	 *                        String teilen sich mk-gateway und mk-kataloge, da mk-kataloge keine Benutzerverwaltung hat.
	 * @param  requestPayload
	 *                        SchulePayload
	 * @return                Response mit SchulePayload
	 */
	@POST
	@Path("/schulen")
	public Response renameSchule(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret, final SchulePayload requestPayload) {

		return null;
	}

	/**
	 * Legt eine neue Schule an, ggf. mit neuem Ort / Land und gibt die neue Schule so zurück. Bei einem konkurrierenden Update wird
	 * das aktuell neueste Item zurückgegeben, damit es sofort im Editor angezeigt werden kann.
	 *
	 * @param  adminUuid
	 *                        String: die UUID des angemeldeten ADMINs
	 * @param  secret
	 *                        String teilen sich mk-gateway und mk-kataloge, da mk-kataloge keine Benutzerverwaltung hat.
	 * @param  requestPayload
	 *                        SchulePayload
	 * @return                Response mit SchulePayload
	 */
	@PUT
	@Path("/schulen")
	public Response createSchule(@HeaderParam(
		value = KatalogAPIApp.UUID_HEADER_NAME) final String adminUuid, @HeaderParam(
			value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret, final SchulePayload requestPayload) {

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
