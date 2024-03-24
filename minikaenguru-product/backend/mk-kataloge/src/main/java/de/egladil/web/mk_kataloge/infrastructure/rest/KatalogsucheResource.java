// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.InvalidProperty;
import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.KatalogItem;
import de.egladil.web.mk_kataloge.domain.KatalogsucheFacade;
import de.egladil.web.mk_kataloge.domain.Katalogtyp;
import de.egladil.web.mk_kataloge.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.domain.event.SecurityIncidentRegistered;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * KatalogsucheResource
 */
@ApplicationScoped
@Path("katalogsuche")
@Produces(MediaType.APPLICATION_JSON)
public class KatalogsucheResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(KatalogsucheResource.class);

	@Inject
	KatalogsucheFacade katalogsucheFacade;

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	@Inject
	LoggableEventDelegate eventDelegate;

	/**
	 *
	 */
	public KatalogsucheResource() {

	}

	@GET
	@Path("global/{typ}")
	@Operation(
		operationId = "findItems", summary = "Gibt alle KatalogItems vom Typ typ zurück, die auf die gegebene Suchanfrage passen.")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "typ",
			required = true,
			description = "Katalogtyp: LAND, ORT, SCHULE"),
		@Parameter(
			in = ParameterIn.QUERY,
			required = true,
			name = "search", description = "Suchstring, mit dem nach KatalogItems im Namen gesucht wird."),
	})
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	@APIResponse(
		name = "BadRequest",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response findItems(@PathParam(
		value = "typ") final String typ, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		LOGGER.debug("{} - {}", typ, searchTerm);

		Response response = validateSearchTerm(searchTerm);

		if (response != null) {

			return response;
		}

		try {

			Katalogtyp katalogtyp = Katalogtyp.valueOf(typ.toUpperCase());

			List<KatalogItem> result = new ArrayList<>();

			switch (katalogtyp) {

			case SCHULE:
				result = katalogsucheFacade.sucheSchulenMitNameEnthaltend(searchTerm);
				break;

			case ORT:
				result = katalogsucheFacade.sucheOrteMitNameBeginnendMit(searchTerm);
				break;

			case LAND:
				result = katalogsucheFacade.sucheLaenderMitNameBeginnendMit(searchTerm);
				break;

			default:
				String msg = "Aufruf von findItems mit unerwartetem Katalogtyp " + typ + ": geben leeres result zurück";
				LOGGER.warn(msg);
				eventDelegate.fireDataInconsistencyEvent(msg, dataInconsistencyEvent);
				ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload.error("Unbeannte URL"));
				return Response.status(Status.NOT_FOUND).entity(responsePayload).build();
			}

			ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), result);
			return Response.ok(responsePayload).build();
		} catch (IllegalArgumentException e) {

			String msg = "Aufruf von findItems mit ungültigem typ-Parameter " + typ;
			LOGGER.warn(msg);
			eventDelegate.fireSecurityEvent(msg, securityEvent);
			ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload.error("Fehlerhafte URL: typ=" + typ));
			return Response.status(Status.BAD_REQUEST).entity(responsePayload).build();

		}
	}

	@GET
	@Path("laender/{land}/orte")
	@Operation(
		operationId = "findOrteInLand", summary = "Gibt alle Orte im gegebenen Land zurück, deren Name mit dem Suchstring beginnt.")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "land",
			required = true,
			description = "Kürzel des Lands im Schulkatalog"),
		@Parameter(
			in = ParameterIn.QUERY,
			name = "search",
			required = true,
			description = "Anfangsbuchstaben des Ortsnamens"),
	})
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = ResponsePayload.class)))
	@APIResponse(
		name = "BadRequest",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
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

		if (StringUtils.isBlank(searchTerm)) {

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
	@Operation(
		operationId = "findSchulenInOrt", summary = "Gibt alle Schulen im gegebenen Ort zurück, deren Name den Suchstring enthält.")
	@Parameters({
		@Parameter(
			in = ParameterIn.PATH,
			name = "ort",
			required = true,
			description = "Kürzel des Orts im Schulkatalog"),
		@Parameter(
			in = ParameterIn.QUERY,
			name = "search",
			required = true,
			description = "Teil des Schulnamens"),
	})
	@APIResponse(
		name = "OKResponse",
		responseCode = "200",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(type = SchemaType.ARRAY, implementation = KatalogItem.class)))
	@APIResponse(
		name = "BadRequest",
		responseCode = "400",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ResponsePayload.class)))
	public Response findSchulenInOrt(@Kuerzel @PathParam(
		value = "ort") final String ortKuerzel, @NotBlank @StringLatin @QueryParam("search") final String searchTerm) {

		LOGGER.debug("{} - {}", ortKuerzel, searchTerm);

		Response response = validateSearchTerm(searchTerm);

		if (response != null) {

			return response;
		}
		List<KatalogItem> result = katalogsucheFacade.sucheSchulenInOrtMitNameEnthaltend(ortKuerzel, searchTerm);

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("OK"), result);

		return Response.ok(responsePayload).build();
	}

}
