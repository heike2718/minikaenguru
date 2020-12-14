// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.adv.AdvService;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VertragAdvAPIModel;

/**
 * AdvResource
 */
@RequestScoped
@Path("adv")
@Consumes(MediaType.APPLICATION_JSON)
public class AdvResource {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Context
	SecurityContext securityContext;

	@Inject
	AdvService advService;

	@GET
	@Path("{schulkuerzel}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public Response downloadVertragAuftragsdatenverarbeitung(@PathParam(value = "schulkuerzel") final String schulkuerzel) {

		final DownloadData data = advService.getVertragAuftragsdatenverarbeitung(schulkuerzel,
			securityContext.getUserPrincipal().getName());

		return MkGatewayFileUtils.createDownloadResponse(data);

	}

	@GET
	@Path("vertragstext")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public Response downloadVertragstext() {

		final DownloadData data = advService.getAktuellenVertragstextAlsPdf();

		return MkGatewayFileUtils.createDownloadResponse(data);

	}

	@POST
	public Response createVertragAuftragsdatenverarbeitung(final VertragAdvAPIModel requestPayload) {

		this.advService.createVertragAuftragsdatenverarbeitung(requestPayload,
			securityContext.getUserPrincipal().getName());

		return Response
			.ok(ResponsePayload.messageOnly(MessagePayload.info(applicationMessages.getString("vertragAdv.create.success"))))
			.build();
	}

}
