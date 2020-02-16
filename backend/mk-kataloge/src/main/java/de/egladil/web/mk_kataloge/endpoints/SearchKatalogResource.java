// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.endpoints;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.commons_validation.InvalidProperty;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.InverseKatalogItem;
import de.egladil.web.mk_kataloge.domain.Katalogtyp;
import de.egladil.web.mk_kataloge.persistence.KatalogRepository;

/**
 * SearchKatalogResource
 */
@RequestScoped
@Path("/kataloge")
@Produces(MediaType.APPLICATION_JSON)
public class SearchKatalogResource {

	@Inject
	KatalogRepository katalogRepository;

	/**
	 *
	 */
	public SearchKatalogResource() {

	}

	@GET
	@Path("/orte")
	public Response findOrte(@StringLatin @QueryParam("search") final String searchTerm) {

		if (StringUtils.isBlank(searchTerm)) {

			ResponsePayload payload = new ResponsePayload(MessagePayload.info("Keine Suche: geben leeres Ergebnis zurück"),
				Arrays.asList(new InverseKatalogItem[0]));

			return Response.ok()
				.entity(payload)
				.build();
		}

		if (searchTerm.length() > 100) {

			ResponsePayload payload = new ResponsePayload(MessagePayload.error("Die Eingaben sind nicht korrekt."),
				Arrays.asList(
					new InvalidProperty[] { new InvalidProperty("searchTerm", "darf nicht länger als 100 Zeichen sein", 0) }));

			return Response.status(400)
				.entity(payload)
				.build();
		}

		List<InverseKatalogItem> result = katalogRepository.findKatalogItems(Katalogtyp.ORT, searchTerm);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), result);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("/schulen")
	public Response findSchulen(@StringLatin @QueryParam("search") final String searchTerm) {

		if (StringUtils.isBlank(searchTerm)) {

			ResponsePayload payload = new ResponsePayload(MessagePayload.error("Die Eingaben sind nicht korrekt."),
				Arrays.asList(new InvalidProperty[] { new InvalidProperty("searchTerm", "darf nicht leer sein", 0) }));

			return Response.status(400)
				.entity(payload)
				.build();
		}

		if (searchTerm.length() > 100) {

			ResponsePayload payload = new ResponsePayload(MessagePayload.error("Die Eingaben sind nicht korrekt."),
				Arrays.asList(
					new InvalidProperty[] { new InvalidProperty("searchTerm", "darf nicht länger als 100 Zeichen sein", 0) }));

			return Response.status(400)
				.entity(payload)
				.build();
		}

		List<InverseKatalogItem> result = katalogRepository.findKatalogItems(Katalogtyp.SCHULE, searchTerm);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), result);

		return Response.ok(responsePayload).build();
	}

}
