// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
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
	@Transactional
	public Kind addKind(final Kind kind) {

		if (kind.identifier() != null) {

			throw new IllegalStateException("Das Kind wurde bereits hinzugefuegt mit UUID=" + kind.identifier().identifier());
		}
		PersistentesKind persistentesKind = this.mapFromKindWithoutUuid(kind);

		em.persist(persistentesKind);

		kind.setIdentifier(new Identifier(persistentesKind.getUuid()));

		return kind;
	}

	PersistentesKind mapFromKindWithoutUuid(final Kind kind) {

		PersistentesKind result = new PersistentesKind();

		result.setKlassenstufe(kind.klassenstufe());

		if (kind.klasseID() != null) {

			result.setKlasseUUID(kind.klasseID().identifier());
		}

		if (kind.loesungszettelID() != null) {

			result.setLoesungszettelUUID(kind.loesungszettelID().identifier());
		}

		result.setNachname(kind.nachname());
		result.setSprache(kind.sprache());
		result.setTeilnahmeart(kind.teilnahmeIdentifier().teilnahmeart());
		result.setTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer());
		result.setVorname(kind.vorname());
		result.setZusatz(kind.zusatz());

		return result;

	}

}
