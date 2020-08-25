// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_wettbewerb.domain.auswertungen.LoesungszettelRepository;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.PersistenterImportierterLoesungszettel;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelHibernateRepository
 */
@RequestScoped
public class LoesungszettelHibernateRepository implements LoesungszettelRepository {

	@Inject
	EntityManager em;

	@Override
	public List<PersistenterImportierterLoesungszettel> loadAllImportiert() {

		return em
			.createQuery("select z from PersistenterImportierterLoesungszettel z", PersistenterImportierterLoesungszettel.class)
			.getResultList();
	}

	@Override
	public List<PersistenterLoesungszettel> loadAll() {

		return em.createNamedQuery(PersistenterLoesungszettel.LOAD_ALL, PersistenterLoesungszettel.class).getResultList();
	}

	@Override
	public boolean addLosungszettel(final PersistenterLoesungszettel loesungszettel) {

		em.persist(loesungszettel);

		return true;
	}

	@Override
	public PersistenterImportierterLoesungszettel updateLoesungszettel(final PersistenterImportierterLoesungszettel zettel) {

		return em.merge(zettel);
	}

}
