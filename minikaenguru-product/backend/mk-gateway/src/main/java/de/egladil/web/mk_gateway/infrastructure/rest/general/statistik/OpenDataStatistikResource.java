// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general.statistik;

import static de.egladil.web.mk_gateway.infrastructure.rest.HttpStatus.HTTP_OK;
import static de.egladil.web.mk_gateway.infrastructure.rest.HttpStatus.HTTP_SERVER_ERROR;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungKlassenstufe;
import de.egladil.web.mk_gateway.domain.statistik.ProzentrangEinzelpunktzahlService;
import de.egladil.web.mk_gateway.domain.statistik.StatistikWettbewerbService;
import de.egladil.web.mk_gateway.domain.statistik.api.AnmeldungenAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.api.MedianeAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.api.ProzentrangAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.functions.StringPunkteMapper;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * OpenDataStatistikResource
 */
@RequestScoped
@Path("open-data/statistik")
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "statistik")
public class OpenDataStatistikResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenDataStatistikResource.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	StatistikWettbewerbService statistikWettbewerbService;

	@Inject
	ProzentrangEinzelpunktzahlService prozentrangService;

	@Inject
	DevDelayService delayService;

	@GET
	@Produces({ MediaType.APPLICATION_XML })
	@Path("{jahr}/IKID/xml")
	public Response getGesamtstatistikIKidsFuerJahr(@PathParam(value = "jahr") final String jahr) {

		Response checkResponse = this.checkJahr(jahr, "/open-data/statistik/{jahr}/IKID/xml");

		this.delayService.pause();

		if (checkResponse.getStatus() != 200) {

			return checkResponse;
		}

		WettbewerbID wettbewerbID = new WettbewerbID(jahr);

		return this.getGesamtstatistikWettbewerbKlassenstufeAlsXml(wettbewerbID, Klassenstufe.IKID);

	}

	@GET
	@Produces({ MediaType.APPLICATION_XML })
	@Path("{jahr}/EINS/xml")
	public Response getGesamtstatistikIKlasse1FuerJahr(@PathParam(value = "jahr") final String jahr) {

		this.delayService.pause();

		Response checkResponse = this.checkJahr(jahr, "/open-data/statistik/{jahr}/EINS/xml");

		if (checkResponse.getStatus() != 200) {

			return checkResponse;
		}

		WettbewerbID wettbewerbID = new WettbewerbID(jahr);

		return this.getGesamtstatistikWettbewerbKlassenstufeAlsXml(wettbewerbID, Klassenstufe.EINS);

	}

	@GET
	@Produces({ MediaType.APPLICATION_XML })
	@Path("{jahr}/ZWEI/xml")
	public Response getGesamtstatistikIKlasse2FuerJahr(@PathParam(value = "jahr") final String jahr) {

		this.delayService.pause();

		Response checkResponse = this.checkJahr(jahr, "/open-data/statistik/{jahr}/ZWEI/xml");

		if (checkResponse.getStatus() != 200) {

			return checkResponse;
		}

		WettbewerbID wettbewerbID = new WettbewerbID(jahr);

		return this.getGesamtstatistikWettbewerbKlassenstufeAlsXml(wettbewerbID, Klassenstufe.ZWEI);

	}

	@GET
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	@Path("{jahr}/pdf")
	public Response downloadGesamtstatistikFuerJahr(@PathParam(value = "jahr") final String jahr) {

		this.delayService.pause();

		Response checkJahrResponse = this.checkJahr(jahr, "/open-data/statistik/{jahr}/pdf");

		if (checkJahrResponse.getStatus() != 200) {

			return checkJahrResponse;
		}

		WettbewerbID wettbewerbID = new WettbewerbID(jahr);

		DownloadData file = this.statistikWettbewerbService.erstelleStatistikPDFWettbewerb(wettbewerbID);

		return MkGatewayFileUtils.createDownloadResponse(file);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{jahr}/mediane")
	public Response getMediane(@PathParam(
		value = "jahr") final String jahr) {

		this.delayService.pause();

		Response checkResponse = this.checkJahr(jahr, "/open-data/statistik/{jahr}/mediane");

		if (checkResponse.getStatus() != 200) {

			return checkResponse;
		}

		WettbewerbID wettbewerbID = new WettbewerbID(jahr);

		MedianeAPIModel mediane = statistikWettbewerbService.berechneGesamtmedianeWettbewerb(wettbewerbID);

		if (mediane.size() == 0) {

			String text = MessageFormat.format(applicationMessages.getString("open_data.gesamt.keineDatenVorhanden"),
				new Object[] { jahr });

			return Response.status(404)
				.entity(ResponsePayload.messageOnly(MessagePayload.warn(text)))
				.build();
		}

		return Response.ok(new ResponsePayload(MessagePayload.ok(), mediane)).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("anmeldungen")
	public Response getAnmeldungenUndBeteiligungenAktuellerWettbewerb() {

		this.delayService.pause();

		AnmeldungenAPIModel anmeldungen = statistikWettbewerbService.berechneAnmeldungsstatistikAktuellerWettbewerb();

		return Response.ok(new ResponsePayload(MessagePayload.ok(), anmeldungen)).build();
	}

	@APIResponses({
		@APIResponse(
			description = "Liste der Anmeldungen und Beteiligungen nach Ländern zu einem Wettbewerbsjahr",
			responseCode = HTTP_OK,
			content = {
				@Content(
					mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(
						implementation = AnmeldungenAPIModel.class,
						type = SchemaType.OBJECT,
						description = "Liste der Anmeldungen und Teilnahmen nach Ländern"))
			}),
		@APIResponse(
			description = "Fehler beim Laden der Teilnahmestatistik",
			name = "Error",
			responseCode = HTTP_SERVER_ERROR,
			content = {
				@Content(
					mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(
						implementation = ResponsePayload.class,
						type = SchemaType.OBJECT))
			})

	})
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("teilnahmen")
	public Response getAnmeldungenUndBeteiligungenNachWettbewerbsjahr(@QueryParam(value = "jahr") final Integer jahr) {

		this.delayService.pause();

		Response checkResponse = this.checkJahr(jahr.toString(), "/open-data/statistik/teilnahmen/{jahr}");

		if (checkResponse.getStatus() != 200) {

			return checkResponse;
		}

		ResponsePayload responsePayload = statistikWettbewerbService.getBeteiligungen(jahr);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("prozentrang")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProzentrang(@QueryParam(value = "jahr") final String jahr, @QueryParam(
		value = "klasse") final String klasse, @QueryParam(
			value = "punkte") final String punkte) {

		this.delayService.pause();

		String fehlertext = "Die Anfrage ist ungültig: jahr muss eine Zahl ab 2010 sein, Klasse muss eins von IKID (0), EINS (1) oder ZWEI (2) sein, Punkte werden in der Form 56,5 erwartet. Beispiel  /open-data/statistik/prozentrang?jahr=2019&klasse=ZWEI&punkte=56,5";

		String eingabeparameter = "getProzentrang()- Fehlerhafte Eingabe: jahr=" + jahr + ", klasse=" + klasse + ", punkte="
			+ punkte;

		Integer wettbewerbsjahr = null;

		try {

			wettbewerbsjahr = Integer.valueOf(jahr);
		} catch (NumberFormatException e) {

			throw this.createInvalidInputException(fehlertext, eingabeparameter);
		}

		Integer punkteMal100 = new StringPunkteMapper().apply(punkte);

		if (punkteMal100 == null) {

			throw this.createInvalidInputException(fehlertext, eingabeparameter);
		}

		if (StringUtils.isAllBlank(klasse)) {

			throw this.createInvalidInputException(fehlertext, eingabeparameter);
		}

		Klassenstufe klassenstufe = Klassenstufe.valueOfNumericString(klasse);

		if (klassenstufe == null) {

			try {

				klassenstufe = Klassenstufe.valueOf(klasse.toUpperCase());
			} catch (IllegalArgumentException e1) {

				throw this.createInvalidInputException(fehlertext, eingabeparameter);
			}
		}

		ProzentrangAPIModel responseData = this.prozentrangService.berechneProzentrang(wettbewerbsjahr, klassenstufe,
			punkteMal100.intValue());

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), responseData);
		return Response.ok(responsePayload).build();
	}

	private InvalidInputException createInvalidInputException(final String fehlertext, final String eingabeparameter) {

		LOGGER.error(eingabeparameter);
		return new InvalidInputException(
			ResponsePayload.messageOnly(MessagePayload.error(fehlertext)));
	}

	private Response checkJahr(final String jahr, final String path) {

		try {

			new WettbewerbID(jahr);

			return Response.ok().build();
		} catch (InvalidInputException e) {

			String text = MessageFormat.format(applicationMessages.getString("open_data.gesamt.keineDatenVorhanden"),
				new Object[] { jahr });

			return Response.status(404)
				.entity(ResponsePayload.messageOnly(MessagePayload.warn(text)))
				.build();

		} catch (NumberFormatException e) {

			throw new InvalidInputException(ResponsePayload.messageOnly(
				MessagePayload
					.error("Pfad: " + path + ": jahr ist das Wettbewerbsjahr und muss eine Zahl sein")));
		}
	}

	private Response getGesamtstatistikWettbewerbKlassenstufeAlsXml(final WettbewerbID wettbewerbID, final Klassenstufe klassenstufe) {

		Optional<GesamtpunktverteilungKlassenstufe> optVerteilung = statistikWettbewerbService
			.erstelleStatistikWettbewerbKlassenstufe(wettbewerbID, klassenstufe);

		if (optVerteilung.isEmpty()) {

			String text = MessageFormat.format(applicationMessages.getString("open_data.klassenstufe.keineDatenVorhanden"),
				new Object[] { wettbewerbID.jahr(), klassenstufe.getLabel() });

			return Response.status(404)
				.entity(ResponsePayload.messageOnly(MessagePayload.warn(text)))
				.build();
		}

		return Response.ok(optVerteilung.get()).build();
	}

}
