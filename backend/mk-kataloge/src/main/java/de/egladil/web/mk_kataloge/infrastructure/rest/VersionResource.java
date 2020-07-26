// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * VersionResource
 */
@RequestScoped
@Path("/version")
@Produces(MediaType.APPLICATION_JSON)
public class VersionResource {

	@ConfigProperty(name = "quarkus.application.version")
	String version;

	@ConfigProperty(name = "stage")
	String stage;

	@GET
	public Response getVersion() {

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(version + " - " + stage))).build();

	}

}
