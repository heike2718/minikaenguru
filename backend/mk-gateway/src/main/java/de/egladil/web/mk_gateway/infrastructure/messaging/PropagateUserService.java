// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.signup.LehrerCreated;
import de.egladil.web.mk_gateway.domain.signup.PrivatmenschCreated;

/**
 * PropagateUserService
 */
@ApplicationScoped
public class PropagateUserService {

	private static final Logger LOG = LoggerFactory.getLogger(PropagateUserService.class);

	@Inject
	@RestClient
	MkWettbewerbRestClient mkWettbewerbRestClient;

	public void handleDomainEvent(@Observes final MkGatewayDomainEvent event) {

		if (LehrerCreated.class.getSimpleName().equals(event.typeName())) {

			LehrerCreated lehrerCreated = (LehrerCreated) event;

			this.propagate(lehrerCreated);
			return;
		}

		if (PrivatmenschCreated.class.getSimpleName().equals(event.typeName())) {

			PrivatmenschCreated privatmenschCreated = (PrivatmenschCreated) event;

			this.propagate(privatmenschCreated);
			return;
		}

	}

	/**
	 * @param privatmenschCreated
	 */
	private void propagate(final PrivatmenschCreated privatmenschCreated) {

		Response response = null;

		try {

			response = mkWettbewerbRestClient.createPrivatmensch(privatmenschCreated);

		} catch (WebApplicationException e) {

			if (response != null) {

				ResponsePayload responsePayload = response.readEntity(ResponsePayload.class);
				MessagePayload messagePayload = responsePayload.getMessage();

				LOG.error("Senden des CreateOrUpdatePrivatpersonCommands war nicht erfolgreich: " + messagePayload.getMessage());
			}
		} catch (Exception e) {

			String msg = "Unerwarteter Fehler beim Senden des CreateOrUpdatePrivatpersonCommands: " + e.getMessage();
			LOG.error(msg, e);

		}
	}

	/**
	 * @param lehrerCreated
	 */
	private void propagate(final LehrerCreated lehrerCreated) {

		Response response = null;

		try {

			response = mkWettbewerbRestClient.createLehrer(lehrerCreated);

		} catch (WebApplicationException e) {

			if (response != null) {

				ResponsePayload responsePayload = response.readEntity(ResponsePayload.class);
				MessagePayload messagePayload = responsePayload.getMessage();

				LOG.error("Senden des CreateOrUpdatePrivatpersonCommands war nicht erfolgreich: " + messagePayload.getMessage());
			}
		} catch (Exception e) {

			String msg = "Unerwarteter Fehler beim Senden des CreateOrUpdatePrivatpersonCommands: " + e.getMessage();
			LOG.error(msg, e);

		}

	}

}
