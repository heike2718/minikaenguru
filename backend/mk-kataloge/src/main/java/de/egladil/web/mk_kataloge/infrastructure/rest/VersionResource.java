// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * VersionResource
 */
@RequestScoped
@Path("version")
@Produces(MediaType.APPLICATION_JSON)
public class VersionResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionResource.class);

	@ConfigProperty(name = "quarkus.application.version")
	String version;

	@ConfigProperty(name = "env")
	String env;

	@ConfigProperty(name = "stage")
	String stage;

	@GET
	public Response getVersion() {

		LOGGER.info("running version {} on stage {} and env {}", version, stage, env);
		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(version + " - " + env))).build();

	}
}
