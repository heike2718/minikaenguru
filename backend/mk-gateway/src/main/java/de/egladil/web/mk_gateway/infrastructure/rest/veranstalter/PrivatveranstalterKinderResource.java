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

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.DuplikatWarnungModel;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.Duplikatkontext;
import de.egladil.web.mk_gateway.domain.kinder.PrivatkinderService;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.PrivatkindRequestData;

/**
 * PrivatveranstalterKinderResource
 */
@RequestScoped
@Path("/privatkinder")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PrivatveranstalterKinderResource {

	private static final Logger LOG = LoggerFactory.getLogger(PrivatveranstalterKinderResource.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	PrivatkinderService privatkinderService;

	@Context
	SecurityContext securityContext;

	@GET
	public Response loadKinder() {

		String uuid = securityContext.getUserPrincipal().getName();

		List<KindAPIModel> kinder = privatkinderService.loadAllKinder(uuid);

		return Response.ok(new ResponsePayload(MessagePayload.ok(), kinder)).build();
	}

	@POST
	@Path("duplikate")
	public Response pruefeMehrfacherfassung(final PrivatkindRequestData data) {

		String uuid = securityContext.getUserPrincipal().getName();

		boolean moeglichesDuplikat = privatkinderService.pruefeDublettePrivat(data, uuid);

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
	public Response kindAnlegen(final PrivatkindRequestData data) {

		String uuid = securityContext.getUserPrincipal().getName();

		KindAPIModel result = this.privatkinderService.privatkindAnlegen(data, uuid);

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
	public Response kindAendern(final PrivatkindRequestData data) {

		String uuid = securityContext.getUserPrincipal().getName();

		KindAPIModel result = this.privatkinderService.privatkindAendern(data, uuid);

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
	public Response kindLoeschen(@PathParam(value = "uuid") final String uuid) {

		LOG.debug("Kind mit uuid = {} soll gelöscht werden.", uuid);

		String veranstalterUuid = securityContext.getUserPrincipal().getName();

		KindAPIModel geloeschtesKind = this.privatkinderService.privatkindLoeschen(uuid, veranstalterUuid);

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
