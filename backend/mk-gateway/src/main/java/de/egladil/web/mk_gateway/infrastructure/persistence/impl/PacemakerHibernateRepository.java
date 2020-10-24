// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.health.PacemakerRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.Pacemaker;

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
