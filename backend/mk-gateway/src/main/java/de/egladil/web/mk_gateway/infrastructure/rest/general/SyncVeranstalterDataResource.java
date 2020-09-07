// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.ChangeUserCommand;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.veranstalter.SynchronizeVeranstalterService;

/**
 * SyncVeranstalterDataResource
 */
@Path(value = "/sync")
public class SyncVeranstalterDataResource {

	private static final Logger LOG = LoggerFactory.getLogger(SyncVeranstalterDataResource.class);

	@ConfigProperty(name = "mkv-app.client-id")
	String clientId;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	@Inject
	SynchronizeVeranstalterService syncService;

	@Path("/veranstalter")
	@POST
	public Response synchronizeVeranstalter(final ChangeUserCommand data) {

		if (!clientId.equals(data.sendingClientId())) {

			String msg = "Aufruf /sync/veranstalter mit falscher ClientID '" + data.sendingClientId() + "'";
			LOG.warn("{}: {}", msg, data);
			new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);
			throw new AccessDeniedException(msg);
		}

		this.syncService.changeVeranstalterDaten(data);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.ok())).build();
	}

}
