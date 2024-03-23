// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.DuplikatWarnungModel;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.Duplikatkontext;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.kinder.KlassenService;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteImportService;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * KlassenResource
 */
@RequestScoped
@Path("klassen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class KlassenResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Context
	SecurityContext securityContext;

	@Inject
	KlassenService klassenService;

	@Inject
	KlassenlisteImportService klassenlisteImportService;

	@Inject
	DevDelayService delayService;

	@GET
	@Path("{schulkuerzel}")
	public Response getKlassen(@PathParam(value = "schulkuerzel") @UuidString final String schulkuerzel) {

		this.delayService.pause();

		String lehrerUuid = securityContext.getUserPrincipal().getName();

		List<KlasseAPIModel> klassen = klassenService.klassenZuSchuleLaden(schulkuerzel, lehrerUuid);

		return Response.ok(new ResponsePayload(MessagePayload.ok(), klassen)).build();
	}

	@POST
	@Path("duplikate")
	public Response pruefeMehrfacherfassung(final KlasseRequestData data) {

		this.delayService.pause();

		String lehrerUuid = securityContext.getUserPrincipal().getName();

		boolean koennteDuplikatSein = klassenService.pruefeDuplikat(data, lehrerUuid);

		String msg = "";

		if (koennteDuplikatSein) {

			msg = MessageFormat.format(applicationMessages.getString("checkKlasseDuplikat"), new Object[] { data.klasse().name() });
		}

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(),
			new DuplikatWarnungModel(Duplikatkontext.KLASSE, msg));
		return Response.ok(responsePayload).build();
	}

	@POST
	public Response klasseAnlegen(final KlasseRequestData data) {

		this.delayService.pause();

		String lehrerUuid = securityContext.getUserPrincipal().getName();

		KlasseAPIModel klasse = this.klassenService.klasseAnlegen(data, lehrerUuid);

		String msg = MessageFormat.format(applicationMessages.getString("saveKlasse.success"),
			new Object[] { klasse.name() });

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), klasse);

		return Response.ok(responsePayload).build();
	}

	@PUT
	public Response klasseAendern(final KlasseRequestData data) {

		this.delayService.pause();

		String lehrerUuid = securityContext.getUserPrincipal().getName();

		KlasseAPIModel klasse = this.klassenService.klasseUmbenennen(data, lehrerUuid);

		String msg = MessageFormat.format(applicationMessages.getString("saveKlasse.success"),
			new Object[] { klasse.name() });

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), klasse);

		return Response.ok(responsePayload).build();
	}

	@DELETE
	@Path("{uuid}")
	public Response klasseLoeschen(@PathParam(value = "uuid") final String uuid) {

		this.delayService.pause();

		String lehrerUuid = securityContext.getUserPrincipal().getName();

		KlasseAPIModel geloeschteKlasse = this.klassenService.klasseLoeschen(uuid, lehrerUuid);

		String msg = MessageFormat.format(applicationMessages.getString("deleteKlasse.success"),
			new Object[] { geloeschteKlasse.name() });

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), geloeschteKlasse);

		return Response.ok(responsePayload).build();
	}

	@GET
	@Path("importreport/{uuid}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public Response downloadImportReport(@PathParam(value = "uuid") final String reportUuid) {

		this.delayService.pause();

		String lehrerUuid = securityContext.getUserPrincipal().getName();

		final DownloadData data = klassenlisteImportService.getImportReport(lehrerUuid, reportUuid);

		return MkGatewayFileUtils.createDownloadResponse(data);
	}
}
