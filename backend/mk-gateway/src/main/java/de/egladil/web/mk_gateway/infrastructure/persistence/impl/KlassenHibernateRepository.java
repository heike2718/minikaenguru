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
import de.egladil.web.mk_gateway.domain.klassen.Klasse;
import de.egladil.web.mk_gateway.domain.klassen.KlassenRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenteKlasse;

/**
 * KlassenHibernateRepository
 */
@RequestScoped
public class KlassenHibernateRepository implements KlassenRepository {

	@Inject
	EntityManager em;

	public static KlassenHibernateRepository createForIntegrationTest(final EntityManager em) {

		KlassenHibernateRepository result = new KlassenHibernateRepository();
		result.em = em;
		return result;
	}

	@Override
	public Optional<Klasse> ofIdentifier(final Identifier klasseID) {

		PersistenteKlasse persistenteKlasse = em.find(PersistenteKlasse.class, klasseID.identifier());

		if (persistenteKlasse == null) {

			return Optional.empty();
		}

		Klasse klasse = mapFromDB(persistenteKlasse);

		return Optional.of(klasse);
	}

	@Override
	public List<Klasse> findKlassenWithSchule(final Identifier schuleID) {

		List<PersistenteKlasse> persistenteKlassen = em
			.createNamedQuery(PersistenteKlasse.FIND_KLASSEN_WITH_SCHULE, PersistenteKlasse.class)
			.setParameter("schulkuerzel", schuleID.identifier()).getResultList();

		if (persistenteKlassen.isEmpty()) {

			return new ArrayList<>();
		}

		return persistenteKlassen.stream().map(kl -> mapFromDB(kl)).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Klasse addKlasse(final Klasse klasse) {

		PersistenteKlasse persistenteKlasse = new PersistenteKlasse();
		persistenteKlasse.setName(klasse.name());
		persistenteKlasse.setSchulkuerzel(klasse.schuleID().identifier());

		em.persist(persistenteKlasse);

		return mapFromDB(persistenteKlasse);
	}

	Klasse mapFromDB(final PersistenteKlasse persistenteKlasse) {

		return new Klasse(new Identifier(persistenteKlasse.getUuid())).withName(persistenteKlasse.getName())
			.withSchuleID(new Identifier(persistenteKlasse.getSchulkuerzel()));
	}

}
