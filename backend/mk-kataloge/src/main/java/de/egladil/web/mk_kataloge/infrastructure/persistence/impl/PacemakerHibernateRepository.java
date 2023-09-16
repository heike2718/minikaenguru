// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.impl;

import de.egladil.web.mk_kataloge.domain.health.PacemakerRepository;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Pacemaker;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

/**
 * PacemakerHibernateRepository
 */
@RequestScoped
public class PacemakerHibernateRepository implements PacemakerRepository {

	@Inject
	EntityManager entityManager;

	@Override
	public Pacemaker findById(final String monitoringId) {

		return entityManager.find(Pacemaker.class, monitoringId);
	}

	@Override
	public Pacemaker change(final Pacemaker pacemaker) {

		return entityManager.merge(pacemaker);
	}

}
