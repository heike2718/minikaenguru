// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.impl;

import de.egladil.web.mk_kataloge.domain.event.EventRepository;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.StoredEvent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * EventHibernateRepository
 */
@RequestScoped
public class EventHibernateRepository implements EventRepository {

	@Inject
	EntityManager em;

	@Override
	@Transactional
	public void appendEvent(final StoredEvent event) {

		this.em.persist(event);

	}
}
