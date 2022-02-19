// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.ConcurrentModificationType;
import de.egladil.web.mk_gateway.domain.error.EntityConcurrentlyModifiedException;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.LoesungszettelNonIdentifiingAttributesMapper;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;
import de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.SortNumberGenerator;
import de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.impl.SortNumberGeneratorImpl;

/**
 * LoesungszettelHibernateRepository
 */
@RequestScoped
public class LoesungszettelHibernateRepository implements LoesungszettelRepository {

	private static final Logger LOG = LoggerFactory.getLogger(LoesungszettelHibernateRepository.class);

	@Inject
	EntityManager entityManager;

	@Inject
	SortNumberGenerator sortNumberGenerator;

	public static LoesungszettelHibernateRepository createForIntegrationTest(final EntityManager em) {

		LoesungszettelHibernateRepository result = new LoesungszettelHibernateRepository();
		result.entityManager = em;
		result.sortNumberGenerator = SortNumberGeneratorImpl.createForIntegrationTests(em);
		return result;
	}

	@Override
	public int anzahlLoesungszettel(final TeilnahmeIdentifier teilnahmeIdentifier) {

		String stmt = "select count(*) from LOESUNGSZETTEL where TEILNAHMENUMMER = :teilnahmenummer and WETTBEWERB_UUID = :wettbewerbUuid and TEILNAHMEART = :teilnahmeart";

		@SuppressWarnings("unchecked")
		List<BigInteger> trefferliste = entityManager.createNativeQuery(stmt)
			.setParameter("teilnahmenummer", teilnahmeIdentifier.teilnahmenummer())
			.setParameter("wettbewerbUuid", teilnahmeIdentifier.wettbewerbID())
			.setParameter("teilnahmeart", teilnahmeIdentifier.teilnahmeart().toString()).getResultList();

		int anzahl = trefferliste.get(0).intValue();

		return anzahl;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pair<Auswertungsquelle, Integer>> getAuswertungsquellenMitAnzahl(final TeilnahmeIdentifier teilnahmeIdentifier) {

		String stmt = "select QUELLE, count(*) from LOESUNGSZETTEL where TEILNAHMENUMMER = :teilnahmenummer and WETTBEWERB_UUID = :wettbewerbUuid and TEILNAHMEART = :teilnahmeart group by QUELLE";

		List<Object[]> trefferliste = entityManager.createNativeQuery(stmt)
			.setParameter("teilnahmenummer", teilnahmeIdentifier.teilnahmenummer())
			.setParameter("wettbewerbUuid", teilnahmeIdentifier.wettbewerbID())
			.setParameter("teilnahmeart", teilnahmeIdentifier.teilnahmeart().toString()).getResultList();

		List<Pair<Auswertungsquelle, Integer>> result = mapQuellenWithAnzahl(trefferliste);
		return result;
	}

	@Override
	public List<Pair<Auswertungsquelle, Integer>> getAuswertungsquelleMitAnzahl(final WettbewerbID wettbewerbID) {

		String stmt = "select QUELLE, count(*) from LOESUNGSZETTEL where WETTBEWERB_UUID = :wettbewerbUuid group by QUELLE";

		@SuppressWarnings("unchecked")
		List<Object[]> trefferliste = entityManager.createNativeQuery(stmt)
			.setParameter("wettbewerbUuid", wettbewerbID.jahr().toString()).getResultList();

		List<Pair<Auswertungsquelle, Integer>> result = mapQuellenWithAnzahl(trefferliste);
		return result;
	}

	private List<Pair<Auswertungsquelle, Integer>> mapQuellenWithAnzahl(final List<Object[]> trefferliste) {

		List<Pair<Auswertungsquelle, Integer>> result = new ArrayList<>();

		for (Object[] obj : trefferliste) {

			Auswertungsquelle auswertungsquelle = Auswertungsquelle.valueOf((String) obj[0]);
			BigInteger bi = (BigInteger) obj[1];

			result.add(Pair.of(auswertungsquelle, bi.intValue()));
		}

		return result;
	}

	@Override
	public List<Loesungszettel> loadAllForWettbewerb(final WettbewerbID wettbewerbID) {

		List<PersistenterLoesungszettel> trefferliste = entityManager
			.createNamedQuery(PersistenterLoesungszettel.LOAD_ALL_WITH_WETTBEWERBID_ASC, PersistenterLoesungszettel.class)
			.setParameter("wettbewerbUuid", wettbewerbID.toString())
			.getResultList();

		return trefferliste.stream().map(pl -> mapFromDB(pl)).collect(Collectors.toList());
	}

	@Override
	public long anzahlForWettbewerb(final WettbewerbID wettbewerbID) {

		String stmt = "select count(*) from LOESUNGSZETTEL where WETTBEWERB_UUID = :wettbewerbUuid";

		@SuppressWarnings("unchecked")
		List<BigInteger> trefferliste = entityManager.createNativeQuery(stmt)
			.setParameter("wettbewerbUuid", wettbewerbID.jahr()).getResultList();

		long anzahl = trefferliste.get(0).longValue();

		return anzahl;
	}

	@Override
	public List<Auspraegung> countAuspraegungenForWettbewerbByColumnName(final Wettbewerb wettbewerb, final String columnName) {

		List<Auspraegung> result = new ArrayList<>();

		String stmt = "select l." + columnName
			+ ", count(*) from LOESUNGSZETTEL l where l.WETTBEWERB_UUID = :wettbewerbUuid  group by l." + columnName;

		// System.out.println(stmt);

		@SuppressWarnings("unchecked")
		List<Object[]> trefferliste = entityManager.createNativeQuery(stmt)
			.setParameter("wettbewerbUuid", wettbewerb.id().jahr().toString())
			.getResultList();

		for (Object[] treffer : trefferliste) {

			String wert = treffer[0].toString();
			BigInteger anzahl = (BigInteger) treffer[1];

			result.add(new Auspraegung(wert, anzahl.longValue()));

		}

		return result;
	}

	@Override
	public List<Loesungszettel> loadLoadPageForWettbewerb(final WettbewerbID wettbewerbID, final int limit, final int offset) {

		List<PersistenterLoesungszettel> trefferliste = entityManager
			.createNamedQuery(PersistenterLoesungszettel.LOAD_ALL_WITH_WETTBEWERBID_DESC, PersistenterLoesungszettel.class)
			.setParameter("wettbewerbUuid", wettbewerbID.jahr().toString())
			.setFirstResult(offset).setMaxResults(limit).getResultList();

		return trefferliste.stream().map(pl -> mapFromDB(pl)).collect(Collectors.toList());
	}

	@Override
	public List<Loesungszettel> loadAllForWettbewerbAndKlassenstufe(final WettbewerbID wettbewerbID, final Klassenstufe klassenstufe) {

		List<PersistenterLoesungszettel> trefferliste = entityManager
			.createNamedQuery(PersistenterLoesungszettel.LOAD_ALL_WITH_WETTBEWERBID_KLASSENSTUFE, PersistenterLoesungszettel.class)
			.setParameter("wettbewerbUuid", wettbewerbID.toString())
			.setParameter("klassenstufe", klassenstufe)
			.getResultList();

		return trefferliste.stream().map(pl -> mapFromDB(pl)).collect(Collectors.toList());
	}

	@Override
	public List<Loesungszettel> loadAll(final TeilnahmeIdentifier teilnahmeIdentifier) {

		List<PersistenterLoesungszettel> trefferliste = entityManager
			.createNamedQuery(PersistenterLoesungszettel.LOAD_ALL_WITH_TEILNAHME_IDENTIFIER, PersistenterLoesungszettel.class)
			.setParameter("teilnahmenummer", teilnahmeIdentifier.teilnahmenummer())
			.setParameter("wettbewerbUuid", teilnahmeIdentifier.wettbewerbID())
			.setParameter("teilnahmeart", teilnahmeIdentifier.teilnahmeart())
			.getResultList();

		return trefferliste.stream().map(pl -> mapFromDB(pl)).collect(Collectors.toList());
	}

	@Override
	public List<Loesungszettel> loadAllWithTeilnahmenummerForWettbewerb(final String teilnahmenummer, final WettbewerbID wettbewerbID) {

		List<PersistenterLoesungszettel> trefferliste = entityManager
			.createNamedQuery(PersistenterLoesungszettel.LOAD_ALL_WITH_TEILNAHMENUMMER_AND_JAHR, PersistenterLoesungszettel.class)
			.setParameter("teilnahmenummer", teilnahmenummer)
			.setParameter("wettbewerbUuid", wettbewerbID.jahr().toString())
			.getResultList();

		return trefferliste.stream().map(pl -> mapFromDB(pl)).collect(Collectors.toList());
	}

	@Override
	public Optional<PersistenterLoesungszettel> findPersistentenLoesungszettel(final Identifier identifier) {

		if (identifier == null) {

			return Optional.empty();
		}

		PersistenterLoesungszettel result = entityManager.find(PersistenterLoesungszettel.class, identifier.identifier());

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

	@Override
	public Optional<Loesungszettel> findLoesungszettelWithKindID(final Identifier kindID) {

		PersistenterLoesungszettel persistenter = this.findEntityWithKindID(kindID);

		if (persistenter == null) {

			return Optional.empty();
		}

		Loesungszettel result = this.mapFromDB(persistenter);

		return Optional.of(result);
	}

	private PersistenterLoesungszettel findEntityWithKindID(final Identifier kindID) {

		if (kindID == null) {

			return null;
		}

		List<PersistenterLoesungszettel> trefferliste = entityManager
			.createNamedQuery(PersistenterLoesungszettel.FIND_LOESUNGSZETTEL_WITH_KIND, PersistenterLoesungszettel.class)
			.setParameter("kindID", kindID.identifier())
			.getResultList();

		if (trefferliste.isEmpty()) {

			return null;
		}

		if (trefferliste.size() > 1) {

			LOG.warn("{} LOESUNGSZETTEL mit KIND_ID={} gefunden. Nehmen den ersten", trefferliste.size(), kindID);
		}

		return trefferliste.get(0);
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
			.withPunkte(persistenter.getPunkte())
			.withRohdaten(rohdaten)
			.withSprache(persistenter.getSprache())
			.withTeilnahmeIdentifier(teilnahmeIdentifier)
			.withVersion(persistenter.getVersion())
			.withSortnumber(persistenter.getSortNumber());

		if (persistenter.getKindID() != null) {

			result = result.withKindID(new Identifier(persistenter.getKindID()));
		}

		return result;
	}

	void copyAllAttributesButIdentifier(final PersistenterLoesungszettel target, final Loesungszettel loesungszettel) {

		new LoesungszettelNonIdentifiingAttributesMapper().copyAllAttributesButIdentifier(target, loesungszettel);
	}

	@Override
	public Loesungszettel addLoesungszettel(final Loesungszettel loesungszettel) throws EntityConcurrentlyModifiedException {

		PersistenterLoesungszettel concurrentlyInserted = this.findEntityWithKindID(loesungszettel.kindID());

		if (concurrentlyInserted != null) {

			Loesungszettel result = this.mapFromDB(concurrentlyInserted);
			throw new EntityConcurrentlyModifiedException(ConcurrentModificationType.INSERTED, result);
		}

		PersistenterLoesungszettel zuPeristierenderLoesungszettel = new PersistenterLoesungszettel();
		this.copyAllAttributesButIdentifier(zuPeristierenderLoesungszettel, loesungszettel);
		long nextSortnumberLoesungszettel = sortNumberGenerator.getNextSortnumberLoesungszettel();
		zuPeristierenderLoesungszettel.setSortNumber(nextSortnumberLoesungszettel);

		entityManager.persist(zuPeristierenderLoesungszettel);

		Loesungszettel result = this.mapFromDB(zuPeristierenderLoesungszettel);

		return result;
	}

	@Override
	public Loesungszettel updateLoesungszettel(final Loesungszettel loesungszettel) {

		if (loesungszettel.identifier() == null) {

			throw new IllegalStateException("loesungszettel hat keine UUID und kann geändert werden!");
		}

		Optional<PersistenterLoesungszettel> optPersistenter = this.findPersistentenLoesungszettel(loesungszettel.identifier());

		if (optPersistenter.isEmpty()) {

			throw new EntityConcurrentlyModifiedException(ConcurrentModificationType.DETETED, null);
		}

		PersistenterLoesungszettel persistenter = optPersistenter.get();

		if (persistenter.getVersion() > loesungszettel.version()) {

			Loesungszettel result = this.mapFromDB(persistenter);

			throw new EntityConcurrentlyModifiedException(ConcurrentModificationType.UPDATED, result);
		}

		this.copyAllAttributesButIdentifier(persistenter, loesungszettel);

		PersistenterLoesungszettel merged = entityManager.merge(persistenter);

		int neueVersion = merged.getVersion() + 1;

		Loesungszettel result = this.mapFromDB(merged).withVersion(neueVersion);

		return result;
	}

	@Override
	public PersistenterLoesungszettel updateLoesungszettelInTransaction(final PersistenterLoesungszettel persistenterLoesungszettel) {

		return entityManager.merge(persistenterLoesungszettel);
	}

	@Override
	public Optional<PersistenterLoesungszettel> removeLoesungszettel(final Identifier identifier) {

		Optional<PersistenterLoesungszettel> optExisting = this.findPersistentenLoesungszettel(identifier);

		if (optExisting.isPresent()) {

			entityManager.remove(optExisting.get());
		}

		return optExisting;
	}

}
