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

	@Override
	@Transactional
	public void appendEvent(final StoredEvent event) {

		this.em.persist(event);

	}

	@Override
	public List<StoredEvent> getEventsNewerThanTime(final LocalDateTime time) {

		return null;
	}

}
