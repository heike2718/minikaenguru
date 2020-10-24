// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * KinderHibernateRepository
 */
@RequestScoped
public class KinderHibernateRepository implements KinderRepository {

	@Override
	public List<Kind> findKinderWithTeilnahme(final TeilnahmeIdentifier teilnahmeIdentifier) {

		return null;
	}

	@Override
	public Kind addKind(final Kind kind) {

		return null;
	}

}
