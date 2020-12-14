// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.KatalogAPIApp;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_kataloge.domain.schulimport.FileResource;
import de.egladil.web.mk_kataloge.domain.schulimport.ImportSchulenService;

/**
 * UploadSchulenCsvResource. Wird als showcase aufgehoben, bis andere permanente upload-services eingebaut sind.
 */
@RequestScoped
@Deprecated(forRemoval = true)
public class UploadSchulenCsvResource {

	private static final Logger LOG = LoggerFactory.getLogger(UploadSchulenCsvResource.class);

	@ConfigProperty(name = "admin.secret")
	String expectedSecret;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	@Inject
	ImportSchulenService importService;

	@POST
	@Path("csv")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(@HeaderParam(
		value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret, @MultipartForm final FileResource input) {

		if (!expectedSecret.equals(secret)) {

			String msg = "Unautorisierter Versuch, eine Datei hochzuladen: " + input.fileName() + ", secret=" + secret;

			LOG.warn(msg);

			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);

			return Response.status(Status.FORBIDDEN)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("Netter Versuch, aber leider keine Berechtigung")))
				.build();

		}

		ResponsePayload responsePayload = importService.schulenImportieren(input);
		return Response.ok(responsePayload).build();
	}
}
