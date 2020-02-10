// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.endpoints;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.persistence.KatalogRepository;

/**
 * KatalogResource
 */
@RequestScoped
@Path("/laender")
@Produces(MediaType.APPLICATION_JSON)
public class KatalogResource {

	@Inject
	KatalogRepository katalogFacade;

	@GET
	public Response getLaender() {

		List<KatalogItem> laender = katalogFacade.loadLaender();

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), laender);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("/{landKuerzel}/orte")
	public Response getOrte(@PathParam(value = "landKuerzel") final String landKuerzel) {

		List<KatalogItem> orte = katalogFacade.loadOrte(landKuerzel);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), orte);

		return Response.ok(responsePayload).build();
	}
}
