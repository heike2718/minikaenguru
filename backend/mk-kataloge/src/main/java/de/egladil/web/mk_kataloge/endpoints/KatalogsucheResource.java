// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.endpoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.InvalidProperty;
import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.Katalogtyp;
import de.egladil.web.mk_kataloge.service.KatalogsucheFacade;

/**
 * KatalogsucheResource
 */
@ApplicationScoped
@Path("/katalogsuche")
@Produces(MediaType.APPLICATION_JSON)
public class KatalogsucheResource {

	private static final Logger LOG = LoggerFactory.getLogger(KatalogsucheResource.class);

	@Inject
	KatalogsucheFacade katalogsucheFacade;

	/**
	 *
	 */
	public KatalogsucheResource() {

	}

	@GET
	@Path("global/{typ}")
	public Response findItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		LOG.debug("{} - {}", typ, searchTerm);

		Response response = validateSearchTerm(searchTerm);

		if (response != null) {

			return response;
		}

		try {

			Katalogtyp katalogtyp = Katalogtyp.valueOf(typ.toUpperCase());

			List<KatalogItem> result = new ArrayList<>();

			switch (katalogtyp) {

			case SCHULE:
				result = katalogsucheFacade.sucheSchulenMitNameBeginnendMit(searchTerm);
				break;

			case ORT:
				result = katalogsucheFacade.sucheOrteMitNameBeginnendMit(searchTerm);
				break;

			case LAND:
				result = katalogsucheFacade.sucheLaenderMitNameBeginnendMit(searchTerm);
				break;

			default:
				LOG.warn("Unerwarteter Katalogtyp {}: geben leeres result zurück", typ);
				ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload.error("Unbeannte URL"));
				return Response.status(Status.NOT_FOUND).entity(responsePayload).build();
			}

			ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), result);
			return Response.ok(responsePayload).build();
		} catch (IllegalArgumentException e) {

			LOG.warn("ungültiger typ-Parameter {}", typ);

			ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload.error("Fehlerhafte URL"));
			return Response.status(Status.NOT_FOUND).entity(responsePayload).build();

		}
	}

	@GET
	@Path("laender/{land}/orte")
	public Response findOrteInLand(@LandKuerzel @PathParam(
		value = "land") final String landKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		Response response = validateSearchTerm(searchTerm);

		if (response != null) {

			return response;
		}

		List<KatalogItem> result = katalogsucheFacade.sucheOrteInLandMitNameBeginnendMit(landKuerzel, searchTerm);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), result);

		return Response.ok(responsePayload).build();
	}

	/**
	 * @param landKuerzel
	 * @param searchTerm
	 */
	private Response validateSearchTerm(final String searchTerm) {

		if (searchTerm.isBlank()) {

			ResponsePayload payload = new ResponsePayload(MessagePayload.error("Die Eingaben sind nicht korrekt."),
				Arrays.asList(
					new InvalidProperty[] { new InvalidProperty("searchTerm", "darf nicht leer sein", 0) }));

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

		return null;
	}

	@GET
	@Path("orte/{ort}/schulen")
	public Response findSchulenInOrt(@Kuerzel @PathParam(
		value = "ort") final String ortKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		LOG.debug("{} - {}", ortKuerzel, searchTerm);

		Response response = validateSearchTerm(searchTerm);

		if (response != null) {

			return response;
		}
		List<KatalogItem> result = katalogsucheFacade.sucheSchulenInOrtMitNameEnthaltend(ortKuerzel, searchTerm);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), result);

		return Response.ok(responsePayload).build();
	}

}
