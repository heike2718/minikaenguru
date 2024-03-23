// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesKind;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesKindKindMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * KinderHibernateRepository
 */
@RequestScoped
public class KinderHibernateRepository implements KinderRepository {

	private static final Logger LOG = LoggerFactory.getLogger(KinderHibernateRepository.class);

	private final PersistentesKindKindMapper dbToDomainObjectMapper = new PersistentesKindKindMapper();

	@Inject
	EntityManager em;

	public static KinderHibernateRepository createForIntegrationTest(final EntityManager em) {

		KinderHibernateRepository result = new KinderHibernateRepository();
		result.em = em;
		return result;
	}

	@Override
	public List<Kind> withTeilnahme(final TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier) {

		List<PersistentesKind> trefferliste = em.createNamedQuery(PersistentesKind.FIND_BY_TEILNAHME, PersistentesKind.class)
			.setParameter("teilnahmenummer", teilnahmeIdentifier.teilnahmenummer())
			.setParameter("teilnahmeart", teilnahmeIdentifier.teilnahmeart())
			.getResultList();

		if (trefferliste.isEmpty()) {

			return new ArrayList<>();
		}

		return trefferliste.stream().map(pk -> dbToDomainObjectMapper.apply(pk)).collect(Collectors.toList());
	}

	@Override
	public List<Kind> withTeilnahmeHavingLoesungszettel(final TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier) {

		List<PersistentesKind> trefferliste = em
			.createNamedQuery(PersistentesKind.FIND_BY_TEILNAHME_WITH_NON_NULL_LOESUNGSZETTEL, PersistentesKind.class)
			.setParameter("teilnahmenummer", teilnahmeIdentifier.teilnahmenummer())
			.setParameter("teilnahmeart", teilnahmeIdentifier.teilnahmeart())
			.getResultList();

		if (trefferliste.isEmpty()) {

			return new ArrayList<>();
		}

		return trefferliste.stream().map(pk -> dbToDomainObjectMapper.apply(pk)).collect(Collectors.toList());
	}

	@Override
	public Optional<Kind> ofId(final Identifier identifier) {

		PersistentesKind persistentesKind = em.find(PersistentesKind.class, identifier.identifier());

		if (persistentesKind == null) {

			return Optional.empty();
		}

		Kind kind = new PersistentesKindKindMapper().apply(persistentesKind);

		return Optional.of(kind);
	}

	@Override
	public Optional<Kind> findKindWithLoesungszettelId(final Identifier loesungszettelID) {

		if (loesungszettelID == null) {

			return Optional.empty();
		}

		List<PersistentesKind> trefferliste = em.createNamedQuery(PersistentesKind.FIND_WITH_LOESUNGSZETTEL, PersistentesKind.class)
			.setParameter("loesungszettelUUID", loesungszettelID.identifier()).getResultList();

		if (trefferliste.isEmpty()) {

			return Optional.empty();
		}

		if (trefferliste.size() > 1) {

			LOG.warn("{} kinder mit LOESUNGSZETTEL_UUID={} vorhanden. Geben erstes zurück", trefferliste.size(), loesungszettelID);
		}

		Kind kind = new PersistentesKindKindMapper().apply(trefferliste.get(0));

		return Optional.of(kind);
	}

	@Override
	@Transactional
	public Kind addKind(final Kind kind) {

		if (kind.identifier() != null) {

			throw new IllegalStateException("Das Kind wurde bereits hinzugefuegt mit UUID=" + kind.identifier().identifier());
		}
		PersistentesKind persistentesKind = new PersistentesKind();
		new KindPersistentesKindAttributeMapper().copyAttributesFromKindWithoutUuid(kind, persistentesKind);

		em.persist(persistentesKind);

		Kind result = dbToDomainObjectMapper.apply(persistentesKind);
		return result;
	}

	@Override
	public boolean changeKind(final Kind kind) {

		PersistentesKind persistentesKind = em.find(PersistentesKind.class, kind.identifier().identifier());

		if (persistentesKind == null) {

			return false;
		}

		new KindPersistentesKindAttributeMapper().copyAttributesFromKindWithoutUuid(kind, persistentesKind);

		em.merge(persistentesKind);

		return true;
	}

	@Override
	public boolean removeKind(final Kind kind) {

		PersistentesKind persistentesKind = em.find(PersistentesKind.class, kind.identifier().identifier());

		if (persistentesKind == null) {

			return false;
		}

		em.remove(persistentesKind);
		return true;

	}

	@Override
	public int removeKinder(final List<Kind> kinder) {

		int count = 0;

		for (Kind kind : kinder) {

			boolean removed = this.removeKind(kind);

			if (removed) {

				count++;
			}
		}

		return count;
	}

	@Override
	public long countKinderZuTeilnahme(final TeilnahmeIdentifier teilnahmeIdentifier) {

		String stmt = "select count(*) from KINDER k where k.TEILNAHMENUMMER = :teilnahmenummer";

		@SuppressWarnings("unchecked")
		List<Long> trefferliste = em.createNativeQuery(stmt)
			.setParameter("teilnahmenummer", teilnahmeIdentifier.teilnahmenummer()).getResultList();

		if (trefferliste.isEmpty()) {

			return 0;
		}

		return trefferliste.get(0).longValue();
	}

	@Override
	public long countKinderInKlasse(final Klasse klasse) {

		String stmt = "select count(*) from KINDER k where k.KLASSE_UUID = :klasseUuid";

		@SuppressWarnings("unchecked")
		List<Long> trefferliste = em.createNativeQuery(stmt)
			.setParameter("klasseUuid", klasse.identifier().identifier()).getResultList();

		if (trefferliste.isEmpty()) {

			return 0;
		}

		return trefferliste.get(0).longValue();
	}

	@Override
	public long countKinderZuPruefen(final Klasse klasse) {

		String stmt = "select count(*) from KINDER k where k.KLASSE_UUID = :klasseUuid and (k.KLASSENSTUFE_PRUEFEN = :klassenstufePruefen || k.DUBLETTE_PRUEFEN = :dublettePruefen)";

		@SuppressWarnings("unchecked")
		List<Long> trefferliste = em.createNativeQuery(stmt)
			.setParameter("klasseUuid", klasse.identifier().identifier())
			.setParameter("klassenstufePruefen", 1)
			.setParameter("dublettePruefen", 1)
			.getResultList();

		if (trefferliste.isEmpty()) {

			return 0;
		}

		return trefferliste.get(0).longValue();
	}

	@Override
	public long countLoesungszettelInKlasse(final Klasse klasse) {

		String stmt = "select count(*) from KINDER k where k.KLASSE_UUID = :klasseUuid AND k.LOESUNGSZETTEL_UUID IS NOT NULL";

		@SuppressWarnings("unchecked")
		List<Long> trefferliste = em.createNativeQuery(stmt)
			.setParameter("klasseUuid", klasse.identifier().identifier()).getResultList();

		if (trefferliste.isEmpty()) {

			return 0;
		}

		return trefferliste.get(0).longValue();
	}

	@Override
	public List<Auspraegung> countAuspraegungenByColumnName(final String columnName) {

		List<Auspraegung> result = new ArrayList<>();

		String stmt = "select k." + columnName + ", count(*) from KINDER k group by k." + columnName;

		// System.out.println(stmt);

		@SuppressWarnings("unchecked")
		List<Object[]> trefferliste = em.createNativeQuery(stmt).getResultList();

		for (Object[] treffer : trefferliste) {

			String wert = treffer[0].toString();
			Long anzahl = (Long) treffer[1];

			result.add(new Auspraegung(wert, anzahl.longValue()));

		}

		return result;
	}
}
