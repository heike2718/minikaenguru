// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.InaccessableEndpointException;
import de.egladil.web.mk_gateway.domain.error.LogmessagePrefixes;
import de.egladil.web.mk_gateway.domain.health.HeartbeatService;

/**
 * HeartbeatResource
 */
@ApplicationScoped
@Path("/heartbeats")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HeartbeatResource {

	private static final Logger LOG = LoggerFactory.getLogger(HeartbeatResource.class);

	@Inject
	HeartbeatService heartbeatService;

	@ConfigProperty(name = "heartbeat.id")
	String expectedHeartbeatId;

	@GET
	public Response check(@QueryParam("heartbeatId") final String heartbeatId) {

		if (!expectedHeartbeatId.equals(heartbeatId)) {

			LOG.warn(LogmessagePrefixes.BOT + "Aufruf mit fehlerhaftem QueryParam " + heartbeatId);
			return Response.status(401)
				.entity(ResponsePayload.messageOnly(MessagePayload.error("keine Berechtigung für diese Resource"))).build();
		}

		try {

			ResponsePayload responsePayload = heartbeatService.updatePacemaker();
			return Response.ok(responsePayload).build();
		} catch (InaccessableEndpointException e) {

			LOG.error(e.getMessage(), e);
			return Response.serverError().entity(ResponsePayload.messageOnly(MessagePayload.error(e.getMessage()))).build();
		} catch (Exception e) {

			String msg = "Fehler beim Speichern des pacemakers " + HeartbeatService.MK_GATEWAY_PACEMAKER_ID + ": " + e.getMessage();
			LOG.error("Fehler beim updaten des pacemakers: " + e.getMessage(), e);
			return Response.serverError().entity(ResponsePayload.messageOnly(MessagePayload.error(msg))).build();
		}
	}

}
