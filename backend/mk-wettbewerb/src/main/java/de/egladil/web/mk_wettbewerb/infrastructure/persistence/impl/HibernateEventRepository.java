// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import de.egladil.web.mk_wettbewerb.domain.event.EventRepository;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.StoredEvent;

/**
 * HibernateEventRepository
 */
@RequestScoped
public class HibernateEventRepository implements EventRepository {

	@Inject
	EntityManager em;

	@Override
	@Transactional
	public void appendEvent(final StoredEvent event) {

		this.em.persist(event);

	}
}
