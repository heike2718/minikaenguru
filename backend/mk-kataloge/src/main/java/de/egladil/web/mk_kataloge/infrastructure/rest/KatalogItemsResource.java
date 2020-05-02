// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.application.KatalogFacade;
import de.egladil.web.mk_kataloge.domain.KatalogItem;

/**
 * KatalogItemsResource
 */
@ApplicationScoped
@Path("/kataloge")
@Produces(MediaType.APPLICATION_JSON)
public class KatalogItemsResource {

	@Inject
	KatalogFacade katalogFacade;

	@GET
	@Path("laender/{kuerzel}/orte")
	public Response loadOrteInLand(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		int anzahlTreffer = katalogFacade.countOrteInLand(kuerzel);

		if (anzahlTreffer > 10) {

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
	@Path("orte/{kuerzel}/schulen")
	public Response loadSchulenInOrt(@PathParam(value = "kuerzel") @Kuerzel final String kuerzel) {

		int anzahlTreffer = katalogFacade.countSchulenInOrt(kuerzel);

		if (anzahlTreffer > 10) {

			ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload
				.warn("Die Abfrage ergibt zu viele Treffer. Bitte verwenden Sie die Suche /mk-kataloge-api/katalogsuche/orte/"
					+ kuerzel + "/schulen?search="));

			return Response.status(Status.BAD_REQUEST).entity(responsePayload).build();
		}

		List<KatalogItem> trefferliste = katalogFacade.loadSchulenInOrt(kuerzel);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), trefferliste);

		return Response.ok(responsePayload).build();
	}
}
