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
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.events.LoesungszettelDeleted;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelHibernateRepository
 */
@RequestScoped
public class LoesungszettelHibernateRepository implements LoesungszettelRepository {

	@Inject
	EntityManager em;

	@Inject
	Event<LoesungszettelDeleted> loesungszettelDeletedEvent;

	private LoesungszettelDeleted loesungszettelDeleted;

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
	public Optional<PersistenterLoesungszettel> findByIdentifier(final Identifier identifier) {

		PersistenterLoesungszettel result = em.find(PersistenterLoesungszettel.class, identifier.identifier());

		return result == null ? Optional.empty() : Optional.of(result);
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

	PersistenterLoesungszettel mapFromDomainObject(final Loesungszettel loesungszettel) {

		LoesungszettelRohdaten rohdaten = loesungszettel.rohdaten();
		PersistenterLoesungszettel result = new PersistenterLoesungszettel()
			.withAntwortcode(rohdaten.antwortcode())
			.withAuswertungsquelle(loesungszettel.auswertungsquelle())
			.withKaengurusprung(loesungszettel.laengeKaengurusprung())
			.withKindID(loesungszettel.kindID().identifier())
			.withKlassenstufe(loesungszettel.klassenstufe())
			.withLandkuerzel(loesungszettel.landkuerzel())
			.withNutzereingabe(rohdaten.nutzereingabe())
			.withPunkte(loesungszettel.punkte())
			.withSprache(loesungszettel.sprache())
			.withTeilnahmeart(loesungszettel.teilnahmeIdentifier().teilnahmeart())
			.withTeilnahmenummer(loesungszettel.teilnahmeIdentifier().teilnahmenummer())
			.withTypo(rohdaten.hatTypo())
			.withWertungscode(rohdaten.wertungscode())
			.withWettbewerbUuid(loesungszettel.teilnahmeIdentifier().wettbewerbID());

		if (loesungszettel.identifier() != null) {

			result.setUuid(loesungszettel.identifier().identifier());
		}
		return result;

	}

	@Override
	public Identifier addLosungszettel(final Loesungszettel loesungszettel) {

		if (loesungszettel.identifier() != null) {

			throw new IllegalStateException("loesungszettel hat bereits die UUID " + loesungszettel.identifier().identifier()
				+ " und kann hinzugefügt werden!");
		}

		PersistenterLoesungszettel zuPeristierenderLoesungszettel = this.mapFromDomainObject(loesungszettel);

		em.persist(zuPeristierenderLoesungszettel);

		return new Identifier(zuPeristierenderLoesungszettel.getUuid());
	}

	@Override
	public boolean updateLoesungszettel(final Loesungszettel loesungszettel) {

		if (loesungszettel.identifier() == null) {

			throw new IllegalStateException("loesungszettel hat keine UUID und kann geändert werden!");
		}

		PersistenterLoesungszettel neuerLoesungszettel = this.mapFromDomainObject(loesungszettel);
		this.removeLoesungszettel(loesungszettel.identifier(), null);
		neuerLoesungszettel.setImportierteUuid(loesungszettel.identifier().identifier());

		em.persist(neuerLoesungszettel);

		return true;
	}

	@Override
	public PersistenterLoesungszettel updateLoesungszettelInTransaction(final PersistenterLoesungszettel persistenterLoesungszettel) {

		return em.merge(persistenterLoesungszettel);
	}

	@Override
	public boolean removeLoesungszettel(final Identifier identifier, final String veranstalterUuid) {

		Optional<PersistenterLoesungszettel> optExisting = this.findByIdentifier(identifier);

		if (optExisting.isEmpty()) {

			return false;
		}

		PersistenterLoesungszettel persistenterLoesungszettel = optExisting.get();

		em.remove(persistenterLoesungszettel);

		if (veranstalterUuid != null) {

			Sprache sprache = persistenterLoesungszettel.getSprache();

			LoesungszettelRohdaten rohdaten = new LoesungszettelRohdaten()
				.withAntwortcode(persistenterLoesungszettel.getAntwortcode())
				.withNutzereingabe(persistenterLoesungszettel.getNutzereingabe())
				.withTypo(persistenterLoesungszettel.isTypo())
				.withWertungscode(persistenterLoesungszettel.getWertungscode());

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
				.withTeilnahmeart(persistenterLoesungszettel.getTeilnahmeart())
				.withTeilnahmenummer(persistenterLoesungszettel.getTeilnahmenummer())
				.withWettbewerbID(new WettbewerbID(persistenterLoesungszettel.getWettbewerbUuid()));

			loesungszettelDeleted = (LoesungszettelDeleted) new LoesungszettelDeleted(veranstalterUuid)
				.withKindID(persistenterLoesungszettel.getKindID())
				.withRohdatenAlt(rohdaten)
				.withRohdatenNeu(null)
				.withSpracheAlt(sprache)
				.withSpracheNeu(null)
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withUuid(persistenterLoesungszettel.getUuid());

			if (loesungszettelDeletedEvent != null) {

				loesungszettelDeletedEvent.fire(loesungszettelDeleted);
			}
		}

		return true;
	}

}
