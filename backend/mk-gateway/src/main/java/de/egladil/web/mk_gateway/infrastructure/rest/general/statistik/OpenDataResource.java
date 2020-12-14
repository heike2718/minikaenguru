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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungKlassenstufe;
import de.egladil.web.mk_gateway.domain.statistik.StatistikWettbewerbService;
import de.egladil.web.mk_gateway.domain.statistik.api.MedianeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * OpenDataResource
 */
@RequestScoped
@Path("open-data")
@Consumes(MediaType.APPLICATION_JSON)
public class OpenDataResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	StatistikWettbewerbService statistikWettbewerbService;

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
