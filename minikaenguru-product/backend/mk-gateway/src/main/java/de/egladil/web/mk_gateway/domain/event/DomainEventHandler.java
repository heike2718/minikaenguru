// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.StoredEvent;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.EventRepositoryHibernate;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

/**
 * DomainEventHandler
 */
@RequestScoped
public class DomainEventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DomainEventHandler.class);

	@Inject
	EventRepository eventRepository;

	public static DomainEventHandler createForIntegrationTest(final EntityManager em) {

		DomainEventHandler result = new DomainEventHandler();
		result.eventRepository = EventRepositoryHibernate.createForIntegrationTest(em);
		return result;
	}

	public void handleEvent(final MkGatewayDomainEvent event) {

		try {

			String body = new ObjectMapper().writeValueAsString(event);

			LOG.info("Event body = " + body);

			StoredEvent storedEvent = StoredEvent.createEvent(event.occuredOn(), event.typeName(), body);

			storeEventQuietly(body, storedEvent);

		} catch (JsonProcessingException e) {

			throw new MkGatewayRuntimeException("konnte event nicht serialisieren: " + e.getMessage(), e);

		}

	}

	/**
	 * @param body
	 * @param storedEvent
	 */
	void storeEventQuietly(final String body, final StoredEvent storedEvent) {

		try {

			this.eventRepository.appendEvent(storedEvent);

		} catch (PersistenceException e) {

			LOG.info(body);
			LOG.error("PersistenceException beim Speichern eines Events: " + e.getMessage(), e);
		}
	}
}
