// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.adv.AdvService;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VertragAdvAPIModel;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

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

	@Inject
	DevDelayService delayService;

	@GET
	@Path("{schulkuerzel}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public Response downloadVertragAuftragsdatenverarbeitung(@PathParam(value = "schulkuerzel") final String schulkuerzel) {

		this.delayService.pause();

		final DownloadData data = advService.getVertragAuftragsdatenverarbeitung(schulkuerzel,
			securityContext.getUserPrincipal().getName());

		return MkGatewayFileUtils.createDownloadResponse(data);

	}

	@GET
	@Path("vertragstext")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public Response downloadVertragstext() {

		this.delayService.pause();

		final DownloadData data = advService.getAktuellenVertragstextAlsPdf();

		return MkGatewayFileUtils.createDownloadResponse(data);

	}

	@POST
	public Response createVertragAuftragsdatenverarbeitung(final VertragAdvAPIModel requestPayload) {

		this.delayService.pause();

		this.advService.createVertragAuftragsdatenverarbeitung(requestPayload,
			securityContext.getUserPrincipal().getName());

		return Response
			.ok(ResponsePayload.messageOnly(MessagePayload.info(applicationMessages.getString("vertragAdv.create.success"))))
			.build();
	}

}
