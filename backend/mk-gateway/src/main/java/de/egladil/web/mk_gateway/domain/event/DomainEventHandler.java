// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.StoredEvent;

/**
 * DomainEventHandler
 */
@RequestScoped
public class DomainEventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DomainEventHandler.class);

	@Inject
	EventRepository eventRepository;

	public void handleDomainEvent(@Observes final MkGatewayDomainEvent event) {

		try {

			String body = new ObjectMapper().writeValueAsString(event);

			LOG.info("Event body = " + body);

			StoredEvent storedEvent = StoredEvent.createEvent(event.occuredOn(), event.typeName(), body);

			this.eventRepository.appendEvent(storedEvent);

		} catch (JsonProcessingException e) {

			throw new MkGatewayRuntimeException("konnte event nicht serialisieren: " + e.getMessage(), e);

		}

	}

}
