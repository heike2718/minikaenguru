// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.DuplikatWarnungModel;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.Duplikatkontext;
import de.egladil.web.mk_gateway.domain.kinder.KlassenService;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;

/**
 * KlassenResource
 */
@RequestScoped
@Path("/klassen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class KlassenResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Context
	SecurityContext securityContext;

	@Inject
	KlassenService klassenService;

	@GET
	@Path("{schulkuerzel}")
	public Response getKlassen(@PathParam(value = "schulkuerzel") @UuidString final String schulkuerzel) {

		String lehrerUuid = securityContext.getUserPrincipal().getName();

		List<KlasseAPIModel> klassen = klassenService.klassenZuSchuleLaden(schulkuerzel, lehrerUuid);

		return Response.ok(new ResponsePayload(MessagePayload.ok(), klassen)).build();
	}

	@POST
	@Path("duplikate")
	public Response pruefeMehrfacherfassung(final KlasseRequestData data) {

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

		String lehrerUuid = securityContext.getUserPrincipal().getName();

		KlasseAPIModel klasse = this.klassenService.klasseAnlegen(data, lehrerUuid);

		String msg = MessageFormat.format(applicationMessages.getString("saveKlasse.success"),
			new Object[] { klasse.name() });

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), klasse);

		return Response.ok(responsePayload).build();
	}

	@PUT
	public Response klasseAendern(final KlasseRequestData data) {

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

		String lehrerUuid = securityContext.getUserPrincipal().getName();

		KlasseAPIModel geloeschteKlasse = this.klassenService.klasseLoeschen(uuid, lehrerUuid);

		String msg = MessageFormat.format(applicationMessages.getString("deleteKlasse.success"),
			new Object[] { geloeschteKlasse.name() });

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), geloeschteKlasse);

		return Response.ok(responsePayload).build();
	}
}
