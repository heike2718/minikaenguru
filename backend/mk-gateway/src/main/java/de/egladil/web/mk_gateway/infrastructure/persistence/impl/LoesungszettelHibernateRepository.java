// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.math.BigInteger;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.auswertungen.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelHibernateRepository
 */
@RequestScoped
public class LoesungszettelHibernateRepository implements LoesungszettelRepository {

	@Inject
	EntityManager em;

	public static LoesungszettelHibernateRepository createForIntegrationTest(final EntityManager em) {

		LoesungszettelHibernateRepository result = new LoesungszettelHibernateRepository();
		result.em = em;
		return result;
	}

	@Override
	public int anzahlLoesungszettel(final TeilnahmeIdentifier teilnahmeIdentifier) {

		String stmt = "select count(*) from LOESUNGSZETTEL where TEILNAHMENUMMER = :teilnahmenummer and WETTBEWERB_UUID = :wettbewerbUuid and TEILNAHMEART = :teilnahmeart";

		@SuppressWarnings("unchecked")
		List<BigInteger> trefferliste = em.createNativeQuery(stmt)
			.setParameter("teilnahmenummer", teilnahmeIdentifier.teilnahmenummer())
			.setParameter("wettbewerbUuid", teilnahmeIdentifier.wettbewerbID())
			.setParameter("teilnahmeart", teilnahmeIdentifier.teilnahmeart()).getResultList();

		int anzahl = trefferliste.get(0).intValue();

		return anzahl;
	}

	@Override
	public List<PersistenterLoesungszettel> loadAll(final TeilnahmeIdentifier teilnahmeIdentifier) {

		return em.createNamedQuery(PersistenterLoesungszettel.LOAD_ALL_WITH_IDENTIFIER, PersistenterLoesungszettel.class)
			.setParameter("teilnahmenummer", teilnahmeIdentifier.teilnahmenummer())
			.setParameter("wettbewerbUuid", teilnahmeIdentifier.wettbewerbID())
			.setParameter("teilnahmeart", Teilnahmeart.valueOf(teilnahmeIdentifier.teilnahmeart()))
			.getResultList();
	}

	@Override
	public boolean addLosungszettel(final PersistenterLoesungszettel loesungszettel) {

		em.persist(loesungszettel);

		return true;
	}

}
