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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.DuplikatWarnungModel;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.Duplikatkontext;
import de.egladil.web.mk_gateway.domain.kinder.KinderService;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * KinderResource
 */
@RequestScoped
@Path("kinder")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class KinderResource {

	private static final Logger LOG = LoggerFactory.getLogger(KinderResource.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	KinderService kinderService;

	@Inject
	DevDelayService delayService;

	@Context
	SecurityContext securityContext;

	@GET
	@Path("{teilnahmenummer}")
	public Response getKinderMitTeilnahmenummer(@PathParam(value = "teilnahmenummer") @UuidString final String teilnahmenummer) {

		this.delayService.pause();

		String uuid = securityContext.getUserPrincipal().getName();

		List<KindAPIModel> kinder = kinderService.kinderZuTeilnahmeLaden(teilnahmenummer, uuid);

		return Response.ok(new ResponsePayload(MessagePayload.ok(), kinder)).build();
	}

	@POST
	@Path("duplikate")
	public Response pruefeMehrfacherfassung(final KindRequestData data) {

		this.delayService.pause();

		String veranstalterUuid = securityContext.getUserPrincipal().getName();

		boolean moeglichesDuplikat = kinderService.pruefeDublette(data, veranstalterUuid);

		String msg = "";

		if (moeglichesDuplikat) {

			KindEditorModel kind = data.kind();
			msg = this.kinderService.getWarnungstext(kind);

		}

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(),
			new DuplikatWarnungModel(Duplikatkontext.KIND, msg));
		return Response.ok(responsePayload).build();
	}

	@POST
	public Response kindAnlegen(final KindRequestData data) {

		this.delayService.pause();

		String uuid = securityContext.getUserPrincipal().getName();

		KindAPIModel result = this.kinderService.kindAnlegen(data, uuid);

		String msg = "";

		if (StringUtils.isNotBlank(result.nachname())) {

			msg = MessageFormat.format(applicationMessages.getString("saveKindSuccess.vornameNachname"),
				new Object[] { result.vorname(), result.nachname() });
		} else {

			msg = MessageFormat.format(applicationMessages.getString("saveKindSuccess.nurVorname"),
				new Object[] { result.vorname() });
		}

		return Response.ok(new ResponsePayload(MessagePayload.info(msg), result)).build();
	}

	@PUT
	public Response kindAendern(final KindRequestData data) {

		this.delayService.pause();

		String uuid = securityContext.getUserPrincipal().getName();

		KindAPIModel result = this.kinderService.kindAendern(data, uuid);

		String msg = "";

		if (StringUtils.isNotBlank(result.nachname())) {

			msg = MessageFormat.format(applicationMessages.getString("saveKindSuccess.vornameNachname"),
				new Object[] { result.vorname(), result.nachname() });
		} else {

			msg = MessageFormat.format(applicationMessages.getString("saveKindSuccess.nurVorname"),
				new Object[] { result.vorname() });
		}

		return Response.ok(new ResponsePayload(MessagePayload.info(msg), result)).build();
	}

	@DELETE
	@Path("{uuid}")
	public Response kindLoeschen(@PathParam(value = "uuid") @UuidString final String uuid) {

		this.delayService.pause();

		LOG.debug("Kind mit uuid = {} soll gelöscht werden.", uuid);

		String veranstalterUuid = securityContext.getUserPrincipal().getName();

		KindAPIModel geloeschtesKind = this.kinderService.kindLoeschen(uuid, veranstalterUuid);

		String msg = "";

		if (StringUtils.isNotBlank(geloeschtesKind.nachname())) {

			msg = MessageFormat.format(applicationMessages.getString("deleteKindSuccess.vornameNachname"),
				new Object[] { geloeschtesKind.vorname(), geloeschtesKind.nachname() });
		} else {

			msg = MessageFormat.format(applicationMessages.getString("deleteKindSuccess.nurVorname"),
				new Object[] { geloeschtesKind.vorname() });
		}

		return Response.ok(new ResponsePayload(MessagePayload.info(msg), geloeschtesKind)).build();
	}

}
