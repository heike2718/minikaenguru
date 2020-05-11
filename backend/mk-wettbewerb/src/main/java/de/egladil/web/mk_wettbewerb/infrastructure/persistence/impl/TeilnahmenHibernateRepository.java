// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;

import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.TeilnahmenRepository;

/**
 * TeilnahmenHibernateRepository
 */
@RequestScoped
public class TeilnahmenHibernateRepository implements TeilnahmenRepository {

	@Override
	public Optional<Teilnahme> ofIdentifier(final String identifier) {

		return Optional.empty();
	}

}
