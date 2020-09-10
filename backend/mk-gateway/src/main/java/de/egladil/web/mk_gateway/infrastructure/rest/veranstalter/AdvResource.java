// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.text.MessageFormat;

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

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.adv.AdvService;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.VertragAuftragsdatenverarbeitungAPIModel;

/**
 * AdvResource
 */
@RequestScoped
@Path("/adv")
@Consumes(MediaType.APPLICATION_JSON)
public class AdvResource {

	/**
	 *
	 */
	private static final String CONTENT_TYPE_HEADER = "Content-Type";

	private static final String CONTENT_DISPOSITION_HEADER_NAME = "Content-Disposition";

	private static final String CONTENT_DISPOSITION_MF = "attachement; filename={0}";

	@Context
	SecurityContext securityContext;

	@Inject
	AdvService advService;

	@GET
	@Path("/{schulkuerzel}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public Response downloadVertragAuftragsdatenverarbeitung(@PathParam(value = "schulkuerzel") final String schulkuerzel) {

		final DownloadData data = advService.getVertragAuftragsdatenverarbeitung(schulkuerzel,
			securityContext.getUserPrincipal().getName());

		return this.createResponse(data);

	}

	@GET
	@Path("/vertragstext")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public Response downloadVertragstext() {

		final DownloadData data = advService.getAktuellenVertragstextAlsPdf();

		return this.createResponse(data);

	}

	private Response createResponse(final DownloadData data) {

		String filename = data.filename();
		String contentDisposition = MessageFormat.format(CONTENT_DISPOSITION_MF, filename);

		return Response.ok(data.data()).header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_OCTET_STREAM)
			.header(CONTENT_DISPOSITION_HEADER_NAME, contentDisposition).build();
	}

	@POST
	public Response createVertragAuftragsdatenverarbeitung(final VertragAuftragsdatenverarbeitungAPIModel requestPayload) {

		return null;
	}

}
