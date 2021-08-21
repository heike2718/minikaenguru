// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.StoredEvent;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.EventRepositoryHibernate;

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

	@ActivateRequestContext
	public void handleEvent(@Observes final MkGatewayDomainEvent event) {

		try {

			String body = new ObjectMapper().writeValueAsString(event);

			LOG.debug("Event body = " + body);

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
	private void storeEventQuietly(final String body, final StoredEvent storedEvent) {

		try {

			this.eventRepository.appendEvent(storedEvent);

		} catch (PersistenceException e) {

			LOG.info(body);
			LOG.error("PersistenceException beim Speichern eines Events: " + e.getMessage(), e);
		}
	}
}
