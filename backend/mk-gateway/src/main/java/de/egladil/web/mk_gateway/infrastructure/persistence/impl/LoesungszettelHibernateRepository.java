// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.LoesungszettelNonIdentifiingAttributesMapper;
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
			.createNamedQuery(PersistenterLoesungszettel.LOAD_ALL_WITH_TEILNAHME_IDENTIFIER, PersistenterLoesungszettel.class)
			.setParameter("teilnahmenummer", teilnahmeIdentifier.teilnahmenummer())
			.setParameter("wettbewerbUuid", teilnahmeIdentifier.wettbewerbID())
			.setParameter("teilnahmeart", teilnahmeIdentifier.teilnahmeart())
			.getResultList();

		return trefferliste.stream().map(pl -> mapFromDB(pl)).collect(Collectors.toList());
	}

	@Override
	public Optional<PersistenterLoesungszettel> findPersistentenLoesungszettel(final Identifier identifier) {

		PersistenterLoesungszettel result = em.find(PersistenterLoesungszettel.class, identifier.identifier());

		return result == null ? Optional.empty() : Optional.of(result);
	}

	@Override
	public Optional<Loesungszettel> ofID(final Identifier identifier) {

		Optional<PersistenterLoesungszettel> optPersistenter = this.findPersistentenLoesungszettel(identifier);

		if (optPersistenter.isEmpty()) {

			return Optional.empty();
		}

		Loesungszettel result = this.mapFromDB(optPersistenter.get());

		return Optional.of(result);
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
			.withKindID(new Identifier(persistenter.getKindID()))
			.withPunkte(persistenter.getPunkte())
			.withRohdaten(rohdaten)
			.withSprache(persistenter.getSprache())
			.withTeilnahmeIdentifier(teilnahmeIdentifier);

		return result;
	}

	void copyAllAttributesButIdentifier(final PersistenterLoesungszettel target, final Loesungszettel loesungszettel) {

		new LoesungszettelNonIdentifiingAttributesMapper().copyAllAttributesButIdentifier(target, loesungszettel);
	}

	@Override
	public Identifier addLoesungszettel(final Loesungszettel loesungszettel) {

		if (loesungszettel.identifier() != null) {

			throw new IllegalStateException("loesungszettel hat bereits die UUID " + loesungszettel.identifier().identifier()
				+ " und kann hinzugefügt werden!");
		}

		PersistenterLoesungszettel zuPeristierenderLoesungszettel = new PersistenterLoesungszettel();
		this.copyAllAttributesButIdentifier(zuPeristierenderLoesungszettel, loesungszettel);

		em.persist(zuPeristierenderLoesungszettel);

		return new Identifier(zuPeristierenderLoesungszettel.getUuid());
	}

	@Override
	public boolean updateLoesungszettel(final Loesungszettel loesungszettel) {

		if (loesungszettel.identifier() == null) {

			throw new IllegalStateException("loesungszettel hat keine UUID und kann geändert werden!");
		}

		Optional<PersistenterLoesungszettel> optPersistenter = this.findPersistentenLoesungszettel(loesungszettel.identifier());

		if (optPersistenter.isEmpty()) {

			return false;
		}

		PersistenterLoesungszettel persistenter = optPersistenter.get();
		this.copyAllAttributesButIdentifier(persistenter, loesungszettel);

		em.merge(persistenter);

		return true;
	}

	@Override
	public PersistenterLoesungszettel updateLoesungszettelInTransaction(final PersistenterLoesungszettel persistenterLoesungszettel) {

		return em.merge(persistenterLoesungszettel);
	}

	@Override
	public Optional<PersistenterLoesungszettel> removeLoesungszettel(final Identifier identifier, final String veranstalterUuid) {

		Optional<PersistenterLoesungszettel> optExisting = this.findPersistentenLoesungszettel(identifier);

		if (optExisting.isPresent()) {

			em.remove(optExisting.get());
		}

		return optExisting;
	}

}
