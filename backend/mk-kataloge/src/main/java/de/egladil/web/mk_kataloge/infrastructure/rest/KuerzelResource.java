// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.KatalogAPIApp;
import de.egladil.web.mk_kataloge.domain.KuerzelGeneratorService;
import de.egladil.web.mk_kataloge.domain.apimodel.KuerzelAPIModel;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.domain.event.SecurityIncidentRegistered;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * KuerzelResource
 */
@ApplicationScoped
@Path("kuerzel")
@Produces(MediaType.APPLICATION_JSON)
public class KuerzelResource {

	private static final Logger LOG = LoggerFactory.getLogger(KuerzelResource.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@ConfigProperty(name = "admin.secret")
	String expectedSecret;

	@Inject
	KuerzelGeneratorService genenarorService;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	@Inject
	LoggableEventDelegate eventDelegate;

	/**
	 * Gibt ein KuerzelAPIModel zurück.
	 *
	 * @param  secret
	 * @return
	 */
	@GET
	public Response generateKuerzelFuerSchuleUndOrt(@HeaderParam(
		value = KatalogAPIApp.SECRET_HEADER_NAME) final String secret) {

		if (!expectedSecret.equals(secret)) {

			String msg = "Unautorisierter Versuch, Kürzel zu generieren: " + secret;

			LOG.warn(msg);

			eventDelegate.fireSecurityEvent(msg, securityEvent);

			return Response.status(Status.FORBIDDEN).entity(ResponsePayload
				.messageOnly(MessagePayload.error(applicationMessages.getString("general.forbidden")))).build();
		}

		KuerzelAPIModel data = genenarorService.generateKuerzel();

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.ok(), data);

		return Response.ok(responsePayload).build();
	}
}
