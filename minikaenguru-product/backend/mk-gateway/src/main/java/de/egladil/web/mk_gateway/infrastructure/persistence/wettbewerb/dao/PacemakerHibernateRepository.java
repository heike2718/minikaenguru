// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.dao;

import de.egladil.web.mk_gateway.domain.health.PacemakerRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.Pacemaker;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

/**
 * PacemakerHibernateRepository
 */
@RequestScoped
public class PacemakerHibernateRepository implements PacemakerRepository {

	@Inject
	@PersistenceUnit("wettbewerb")
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
