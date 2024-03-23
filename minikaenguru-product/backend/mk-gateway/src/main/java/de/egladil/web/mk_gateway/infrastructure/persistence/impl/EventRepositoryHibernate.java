// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.time.LocalDateTime;
import java.util.List;

import de.egladil.web.mk_gateway.domain.event.EventRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.StoredEvent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * EventRepositoryHibernate
 */
@RequestScoped
public class EventRepositoryHibernate implements EventRepository {

	@Inject
	EntityManager em;

	public static EventRepositoryHibernate createForIntegrationTest(final EntityManager em) {

		EventRepositoryHibernate result = new EventRepositoryHibernate();
		result.em = em;
		return result;
	}

	@Override
	@Transactional
	public void appendEvent(final StoredEvent event) {

		this.em.persist(event);

	}

	@Override
	public List<StoredEvent> findEventsAfter(final LocalDateTime datum) {

		return em.createNamedQuery(StoredEvent.EVENTS_AFTER_DATE, StoredEvent.class).setParameter("date", datum).getResultList();
	}
}
