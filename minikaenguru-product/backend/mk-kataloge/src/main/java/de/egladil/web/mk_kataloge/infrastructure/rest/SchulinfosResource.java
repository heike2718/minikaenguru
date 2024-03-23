// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.application.KatalogFacade;
import de.egladil.web.mk_kataloge.domain.apimodel.SchuleAPIModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * SchulinfosResource
 */
@ApplicationScoped
@Path("schulinfos")
@Produces(MediaType.APPLICATION_JSON)
public class SchulinfosResource {

	private static final Logger LOG = LoggerFactory.getLogger(SchulinfosResource.class);

	@Inject
	KatalogFacade katalogFacade;

	@GET
	@Path("{kommaseparierteKuerzel}")
	public Response findSchulenMitKuerzeln(@PathParam(
		value = "kommaseparierteKuerzel") @Kuerzel final String kommaseparierteKuerzel) {

		List<SchuleAPIModel> trefferliste = katalogFacade.findSchulen(kommaseparierteKuerzel);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), trefferliste);

		return Response.ok(responsePayload).build();
	}

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response loadSchulen(@Kuerzel final String kommaseparierteKuerzel) {

		LOG.debug(StringUtils.abbreviate(kommaseparierteKuerzel, 30));

		List<SchuleAPIModel> trefferliste = katalogFacade.findSchulen(kommaseparierteKuerzel);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), trefferliste);

		return Response.ok(responsePayload).build();
	}

}
