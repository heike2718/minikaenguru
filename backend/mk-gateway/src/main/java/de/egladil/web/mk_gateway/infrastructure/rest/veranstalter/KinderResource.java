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

/**
 * KinderResource
 */
@RequestScoped
@Path("/kinder")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class KinderResource {

	private static final Logger LOG = LoggerFactory.getLogger(KinderResource.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	KinderService kinderService;

	@Context
	SecurityContext securityContext;

	@GET
	@Path("{teilnahmenummer}")
	public Response getKinderMitTeilnahmenummer(@PathParam(value = "teilnahmenummer") @UuidString final String teilnahmenummer) {

		String uuid = securityContext.getUserPrincipal().getName();

		List<KindAPIModel> kinder = kinderService.kinderZuTeilnahmeLaden(teilnahmenummer, uuid);

		return Response.ok(new ResponsePayload(MessagePayload.ok(), kinder)).build();
	}

	@POST
	@Path("duplikate")
	public Response pruefeMehrfacherfassung(final KindRequestData data) {

		String veranstalterUuid = securityContext.getUserPrincipal().getName();

		boolean moeglichesDuplikat = kinderService.pruefeDublette(data, veranstalterUuid);

		String msg = "";

		if (moeglichesDuplikat) {

			KindEditorModel kind = data.kind();

			if (StringUtils.isNotBlank(kind.nachname()) && StringUtils.isNotBlank(kind.zusatz())) {

				msg = MessageFormat.format(applicationMessages.getString("checkKindDuplikat.privat.vornameNachnameZusatz"),
					new Object[] { kind.klassenstufe().label(), kind.vorname(), kind.nachname(), kind.zusatz() });

			} else {

				if (StringUtils.isNotBlank(data.kind().nachname())) {

					msg = MessageFormat.format(applicationMessages.getString("checkKindDuplikat.privat.vornameNachname"),
						new Object[] { kind.klassenstufe().label(), kind.vorname(), kind.nachname() });
				} else {

					msg = MessageFormat.format(applicationMessages.getString("checkKindDuplikat.privat.nurVorname"),
						new Object[] { kind.klassenstufe().label(), kind.vorname() });
				}
			}

		}

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(),
			new DuplikatWarnungModel(Duplikatkontext.KIND, msg));
		return Response.ok(responsePayload).build();
	}

	@POST
	public Response kindAnlegen(final KindRequestData data) {

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
