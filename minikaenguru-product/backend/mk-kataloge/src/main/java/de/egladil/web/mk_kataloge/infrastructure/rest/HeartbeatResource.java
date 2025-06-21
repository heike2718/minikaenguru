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
import de.egladil.web.mk_kataloge.domain.health.HeartbeatService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * HeartbeatResource
 */
@ApplicationScoped
@Path("heartbeats")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HeartbeatResource {

	private static final Logger LOG = LoggerFactory.getLogger(HeartbeatResource.class);

	@Inject
	HeartbeatService heartbeatService;

	@ConfigProperty(name = "heartbeat.id")
	String expectedHeartbeatId;

	@GET
	public Response check(@HeaderParam("X-HEARTBEAT-ID") final String heartbeatId) {

		if (!expectedHeartbeatId.equals(heartbeatId)) {

			LOG.warn("Possible BOT Attack: Aufruf mit fehlerhaftem X-HEARTBEAT-ID-Header value " + heartbeatId);
			return Response.status(401)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("keine Berechtigung für diese Resource"))).build();
		}

		try {

			ResponsePayload responsePayload = heartbeatService.updatePacemaker();
			return Response.ok(responsePayload).build();
		} catch (Exception e) {

			String msg = "Fehler beim Speichern des pacemakers " + HeartbeatService.MONITOR_ID + ": " + e.getMessage();
			LOG.error("Fehler beim updaten des pacemakers: " + e.getMessage(), e);
			return Response.serverError().entity(ResponsePayload.messageOnly(MessagePayload.error(msg))).build();
		}
	}
}
