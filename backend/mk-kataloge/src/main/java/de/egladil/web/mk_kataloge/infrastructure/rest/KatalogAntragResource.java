// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulkatalogAntrag;
import de.egladil.web.mk_kataloge.domain.katalogantrag.KatalogAntragService;

/**
 * KatalogAntragResource
 */
@ApplicationScoped
@Path("katalogantrag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KatalogAntragResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	KatalogAntragService service;

	@POST
	public Response submitSchule(final SchulkatalogAntrag antrag) {

		service.validateAndSend(antrag);

		ResponsePayload payload = ResponsePayload
			.messageOnly(MessagePayload.info(applicationMessages.getString("schulkatalogantrag.success")));

		return Response.ok(payload).build();
	}

}
