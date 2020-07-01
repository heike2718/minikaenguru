// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import de.egladil.web.mk_kataloge.domain.event.EventRepository;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.StoredEvent;

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
