// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
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
			.setParameter("teilnahmeart", teilnahmeIdentifier.teilnahmeart().toString()).getResultList();

		int anzahl = trefferliste.get(0).intValue();

		return anzahl;
	}

	@Override
	public List<Loesungszettel> loadAllForWettbewerb(final WettbewerbID wettbewerbID) {

		List<PersistenterLoesungszettel> trefferliste = em
			.createNamedQuery(PersistenterLoesungszettel.LOAD_ALL_WITH_WETTBEWERBID, PersistenterLoesungszettel.class)
			.setParameter("wettbewerbUuid", wettbewerbID.toString())
			.getResultList();

		return trefferliste.stream().map(pl -> mapFromDB(pl)).collect(Collectors.toList());
	}

	@Override
	public List<Loesungszettel> loadAllForWettbewerbAndKlassenstufe(final WettbewerbID wettbewerbID, final Klassenstufe klassenstufe) {

		List<PersistenterLoesungszettel> trefferliste = em
			.createNamedQuery(PersistenterLoesungszettel.LOAD_ALL_WITH_WETTBEWERBID_KLASSENSTUFE, PersistenterLoesungszettel.class)
			.setParameter("wettbewerbUuid", wettbewerbID.toString())
			.setParameter("klassenstufe", klassenstufe)
			.getResultList();

		return trefferliste.stream().map(pl -> mapFromDB(pl)).collect(Collectors.toList());
	}

	@Override
	public List<Loesungszettel> loadAll(final TeilnahmeIdentifier teilnahmeIdentifier) {

		List<PersistenterLoesungszettel> trefferliste = em
			.createNamedQuery(PersistenterLoesungszettel.LOAD_ALL_WITH_IDENTIFIER, PersistenterLoesungszettel.class)
			.setParameter("teilnahmenummer", teilnahmeIdentifier.teilnahmenummer())
			.setParameter("wettbewerbUuid", teilnahmeIdentifier.wettbewerbID())
			.setParameter("teilnahmeart", teilnahmeIdentifier.teilnahmeart())
			.getResultList();

		return trefferliste.stream().map(pl -> mapFromDB(pl)).collect(Collectors.toList());
	}

	Loesungszettel mapFromDB(final PersistenterLoesungszettel persistenter) {

		WettbewerbID wettbewerbID = new WettbewerbID(persistenter.getWettbewerbUuid());

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
			.withTeilnahmeart(persistenter.getTeilnahmeart())
			.withTeilnahmenummer(persistenter.getTeilnahmenummer())
			.withWettbewerbID(wettbewerbID);

		LoesungszettelRohdaten rohdaten = new LoesungszettelRohdaten()
			.withAntwortcode(persistenter.getAntwortcode())
			.withNutzereingabe(persistenter.getNutzereingabe())
			.withTypo(persistenter.isTypo())
			.withWertungscode(persistenter.getWertungscode());

		Loesungszettel result = new Loesungszettel(new Identifier(persistenter.getUuid()))
			.withAuswertungsquelle(persistenter.getAuswertungsquelle())
			.withKlassenstufe(persistenter.getKlassenstufe())
			.withLaengeKaengurusprung(persistenter.getKaengurusprung())
			.withLandkuerzel(persistenter.getLandkuerzel())
			.withNummer(persistenter.getNummer())
			.withPunkte(persistenter.getPunkte())
			.withRohdaten(rohdaten)
			.withSprache(persistenter.getSprache())
			.withTeilnahmeIdentifier(teilnahmeIdentifier);

		return result;
	}

	@Override
	public boolean addLosungszettel(final PersistenterLoesungszettel loesungszettel) {

		em.persist(loesungszettel);

		return true;
	}

}
