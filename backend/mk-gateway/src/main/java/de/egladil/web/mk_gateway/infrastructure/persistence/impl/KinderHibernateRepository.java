// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

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
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesKind;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesKindKindMapper;

/**
 * KinderHibernateRepository
 */
@RequestScoped
public class KinderHibernateRepository implements KinderRepository {

	@Inject
	EntityManager em;

	public static KinderHibernateRepository createForIntegrationTest(final EntityManager em) {

		KinderHibernateRepository result = new KinderHibernateRepository();
		result.em = em;
		return result;
	}

	@Override
	public List<Kind> findKinderWithTeilnahme(final Teilnahme teilnahme) {

		final PersistentesKindKindMapper mapper = new PersistentesKindKindMapper(teilnahme.wettbewerbID());

		List<PersistentesKind> trefferliste = em.createNamedQuery(PersistentesKind.FIND_BY_TEILNAHME, PersistentesKind.class)
			.setParameter("teilnahmenummer", teilnahme.teilnahmenummer().identifier()).getResultList();

		if (trefferliste.isEmpty()) {

			return new ArrayList<>();
		}

		return trefferliste.stream().map(pk -> mapper.apply(pk)).collect(Collectors.toList());
	}

	@Override
	public Optional<Kind> findKindWithIdentifier(final Identifier identifier, final WettbewerbID wettbewerbID) {

		PersistentesKind persistentesKind = em.find(PersistentesKind.class, identifier.identifier());

		if (persistentesKind == null) {

			return Optional.empty();
		}

		Kind kind = new PersistentesKindKindMapper(wettbewerbID).apply(persistentesKind);

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

		Kind result = this.mapFromDB(persistentesKind, kind.teilnahmeIdentifier());
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

	Kind mapFromDB(final PersistentesKind persistentesKind, final TeilnahmeIdentifier teilnahmeIdentifier) {

		Kind result = new Kind(new Identifier(persistentesKind.getUuid()))
			.withKlassenstufe(persistentesKind.getKlassenstufe())
			.withLandkuerzel(persistentesKind.getLandkuerzel())
			.withNachname(persistentesKind.getNachname())
			.withSprache(persistentesKind.getSprache())
			.withTeilnahmeIdentifier(teilnahmeIdentifier)
			.withVorname(persistentesKind.getVorname())
			.withZusatz(persistentesKind.getZusatz());

		if (persistentesKind.getLoesungszettelUUID() != null) {

			result = result.withLoesungszettelID(new Identifier(persistentesKind.getLoesungszettelUUID()));
		}

		if (persistentesKind.getKlasseUUID() != null) {

			result = result.withKlasseID(new Identifier(persistentesKind.getKlasseUUID()));
		}

		return result;
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
