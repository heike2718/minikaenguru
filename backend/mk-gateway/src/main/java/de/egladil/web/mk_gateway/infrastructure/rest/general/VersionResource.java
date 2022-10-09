// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * VersionResource
 */
@RequestScoped
@Path("version")
@Produces(MediaType.APPLICATION_JSON)
public class VersionResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionResource.class);

	@ConfigProperty(name = "quarkus.application.version", defaultValue = "")
	String version;

	@ConfigProperty(name = "env")
	String env;

	@ConfigProperty(name = "stage")
	String stage;

	@Inject
	DevDelayService delayService;

	@GET
	public Response getVersion() {

		this.delayService.pause();

		String message = "MkGatewayApp running version " + version + " on stage " + stage + " and env " + env;

		LOGGER.info(message);
		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(message))).build();

	}
}
