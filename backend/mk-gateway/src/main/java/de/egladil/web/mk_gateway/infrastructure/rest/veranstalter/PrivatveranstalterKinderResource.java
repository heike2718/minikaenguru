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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang3.StringUtils;

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
	@Path("/duplikate")
	public Response checkDuplikat(final PrivatkindRequestData data) {

		String uuid = securityContext.getUserPrincipal().getName();

		boolean moeglichesDuplikat = privatkinderService.pruefeDublettePrivat(data, uuid);

		String msg = "";

		if (moeglichesDuplikat) {

			KindEditorModel kind = data.kind();

			if (StringUtils.isNotBlank(kind.nachname()) && StringUtils.isNotBlank(kind.zusatz())) {

				msg = MessageFormat.format(applicationMessages.getString("checkKindDuplikat.vornameNachnameZusatz"),
					new Object[] { kind.vorname(), kind.nachname(), kind.zusatz() });

			} else {

				if (StringUtils.isNotBlank(data.kind().nachname())) {

					msg = MessageFormat.format(applicationMessages.getString("checkKindDuplikat.vornameNachname"),
						new Object[] { kind.vorname(), kind.nachname() });
				} else {

					msg = MessageFormat.format(applicationMessages.getString("checkKindDuplikat.nurVorname"),
						new Object[] { kind.vorname() });
				}
			}

		}

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(),
			new DuplikatWarnungModel(Duplikatkontext.KIND, msg));
		return Response.ok(responsePayload).build();
	}

	@POST
	public Response createKind(final PrivatkindRequestData data) {

		String uuid = securityContext.getUserPrincipal().getName();

		KindAPIModel result = this.privatkinderService.privatkindAnlegen(data, uuid);

		String msg = "";

		if (StringUtils.isNotBlank(result.nachname())) {

			msg = MessageFormat.format(applicationMessages.getString("createKindSuccess.vornameNachname"),
				new Object[] { result.vorname(), result.nachname() });
		} else {

			msg = MessageFormat.format(applicationMessages.getString("createKindSuccess.nurVorname"),
				new Object[] { result.vorname() });
		}

		return Response.ok(new ResponsePayload(MessagePayload.info(msg), result)).build();
	}

}
