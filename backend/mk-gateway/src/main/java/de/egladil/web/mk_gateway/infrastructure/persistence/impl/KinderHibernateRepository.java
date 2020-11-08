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
import javax.transaction.Transactional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesKind;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesKindKindMapper;

/**
 * KinderHibernateRepository
 */
@RequestScoped
public class KinderHibernateRepository implements KinderRepository {

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
	public Optional<Kind> withIdentifier(final Identifier identifier) {

		PersistentesKind persistentesKind = em.find(PersistentesKind.class, identifier.identifier());

		if (persistentesKind == null) {

			return Optional.empty();
		}

		Kind kind = new PersistentesKindKindMapper().apply(persistentesKind);

		return Optional.of(kind);
	}

	@Override
	@Transactional
	public Kind addKind(final Kind kind) {

		if (kind.identifier() != null) {

			throw new IllegalStateException("Das Kind wurde bereits hinzugefuegt mit UUID=" + kind.identifier().identifier());
		}
		PersistentesKind persistentesKind = new PersistentesKind();
		this.copyAttributesFromKindWithoutUuid(kind, persistentesKind);

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

		this.copyAttributesFromKindWithoutUuid(kind, persistentesKind);

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
	public long countKinderInKlasse(final Klasse klasse) {

		String stmt = "select count(*) from KINDER k where k.KLASSE_UUID = :klasseUuid";

		@SuppressWarnings("unchecked")
		List<BigInteger> trefferliste = em.createNativeQuery(stmt)
			.setParameter("klasseUuid", klasse.identifier().identifier()).getResultList();

		if (trefferliste.isEmpty()) {

			return 0;
		}

		return trefferliste.get(0).longValue();
	}

	void copyAttributesFromKindWithoutUuid(final Kind source, final PersistentesKind target) {

		target.setKlassenstufe(source.klassenstufe());

		if (source.klasseID() != null) {

			target.setKlasseUUID(source.klasseID().identifier());
		}

		if (source.loesungszettelID() != null) {

			target.setLoesungszettelUUID(source.loesungszettelID().identifier());
		}

		target.setNachname(source.nachname());
		target.setSprache(source.sprache());
		target.setTeilnahmeart(source.teilnahmeIdentifier().teilnahmeart());
		target.setTeilnahmenummer(source.teilnahmeIdentifier().teilnahmenummer());
		target.setVorname(source.vorname());
		target.setZusatz(source.zusatz());
		target.setLandkuerzel(source.landkuerzel());
	}

}
