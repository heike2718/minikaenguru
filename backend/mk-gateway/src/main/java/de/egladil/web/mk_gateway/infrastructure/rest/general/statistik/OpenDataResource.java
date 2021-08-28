// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general.statistik;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
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

/**
 * OpenDataResource
 */
@RequestScoped
@Path("open-data")
@Consumes(MediaType.APPLICATION_JSON)
public class OpenDataResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenDataResource.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	StatistikWettbewerbService statistikWettbewerbService;

	@Inject
	ProzentrangEinzelpunktzahlService prozentrangService;

	@GET
	@Produces({ MediaType.APPLICATION_XML })
	@Path("statistik/{jahr}/IKID/xml")
	public Response getGesamtstatistikIKidsFuerJahr(@PathParam(value = "jahr") final String jahr) {

		Response checkResponse = this.checkJahr(jahr, "/open-data/statistik/{jahr}/IKID/xml");

		if (checkResponse.getStatus() != 200) {

			return checkResponse;
		}

		WettbewerbID wettbewerbID = new WettbewerbID(jahr);

		return this.getGesamtstatistikWettbewerbKlassenstufeAlsXml(wettbewerbID, Klassenstufe.IKID);

	}

	@GET
	@Produces({ MediaType.APPLICATION_XML })
	@Path("statistik/{jahr}/EINS/xml")
	public Response getGesamtstatistikIKlasse1FuerJahr(@PathParam(value = "jahr") final String jahr) {

		Response checkResponse = this.checkJahr(jahr, "/open-data/statistik/{jahr}/EINS/xml");

		if (checkResponse.getStatus() != 200) {

			return checkResponse;
		}

		WettbewerbID wettbewerbID = new WettbewerbID(jahr);

		return this.getGesamtstatistikWettbewerbKlassenstufeAlsXml(wettbewerbID, Klassenstufe.EINS);

	}

	@GET
	@Produces({ MediaType.APPLICATION_XML })
	@Path("statistik/{jahr}/ZWEI/xml")
	public Response getGesamtstatistikIKlasse2FuerJahr(@PathParam(value = "jahr") final String jahr) {

		Response checkResponse = this.checkJahr(jahr, "/open-data/statistik/{jahr}/ZWEI/xml");

		if (checkResponse.getStatus() != 200) {

			return checkResponse;
		}

		WettbewerbID wettbewerbID = new WettbewerbID(jahr);

		return this.getGesamtstatistikWettbewerbKlassenstufeAlsXml(wettbewerbID, Klassenstufe.ZWEI);

	}

	@GET
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	@Path("statistik/{jahr}/pdf")
	public Response downloadGesamtstatistikFuerJahr(@PathParam(value = "jahr") final String jahr) {

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
	@Path("statistik/{jahr}/mediane")
	public Response getMediane(@PathParam(
		value = "jahr") final String jahr) {

		Response checkResponse = this.checkJahr(jahr, "/open-data/statistik/{jahr}/mediane");

		if (checkResponse.getStatus() != 200) {

			return checkResponse;
		}

		WettbewerbID wettbewerbID = new WettbewerbID(jahr);

		Map<Klassenstufe, String> mediane = statistikWettbewerbService.berechneGesamtmedianeWettbewerb(wettbewerbID);

		if (mediane.size() == 0) {

			String text = MessageFormat.format(applicationMessages.getString("open_data.gesamt.keineDatenVorhanden"),
				new Object[] { jahr });

			return Response.status(404)
				.entity(ResponsePayload.messageOnly(MessagePayload.warn(text)))
				.build();
		}

		MedianeAPIModel responseData = new MedianeAPIModel(mediane);

		return Response.ok(new ResponsePayload(MessagePayload.ok(), responseData)).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("statistik/anmeldungen")
	public Response getAnmeldungenUndBeteiligungenAktuellerWettbewerb() {

		AnmeldungenAPIModel anmeldungen = statistikWettbewerbService.berechneAnmeldungsstatistikAktuellerWettbewerb();

		return Response.ok(new ResponsePayload(MessagePayload.ok(), anmeldungen)).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("statistik/teilnahmen")
	public Response getAnmeldungenUndBeteiligungenNachWettbewerbsjahr(@QueryParam(value = "jahr") final Integer jahr) {

		ResponsePayload responsePayload = statistikWettbewerbService.berechneTeilnahmestatistikWettbewerbsjahr(jahr);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("statistik/prozentrang")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProzentrang(@QueryParam(value = "jahr") final String jahr, @QueryParam(
		value = "klasse") final String klasse, @QueryParam(
			value = "punkte") final String punkte) {

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
