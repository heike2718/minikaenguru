// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import de.egladil.web.mk_gateway.domain.event.EventRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.StoredEvent;

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
