// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.PersistenteKlasse;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * KlassenHibernateRepository
 */
@RequestScoped
public class KlassenHibernateRepository implements KlassenRepository {

	@Inject
	@PersistenceUnit("wettbewerb")
	EntityManager em;

	public static KlassenHibernateRepository createForIntegrationTest(final EntityManager em) {

		KlassenHibernateRepository result = new KlassenHibernateRepository();
		result.em = em;
		return result;
	}

	@Override
	public Optional<Klasse> ofIdentifier(final Identifier klasseID) {

		PersistenteKlasse persistenteKlasse = findPersistenteKlasse(klasseID);

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

		Identifier identifier = new Identifier(persistenteKlasse.getUuid());
		String name = persistenteKlasse.getName();
		String schulkuerzel = persistenteKlasse.getSchulkuerzel();
		return new Klasse(identifier).withName(name)
			.withSchuleID(new Identifier(schulkuerzel));
	}

	@Override
	@Transactional
	public Klasse changeKlasse(final Klasse klasse) {

		PersistenteKlasse persistenteKlasse = findPersistenteKlasse(klasse.identifier());
		persistenteKlasse.setName(klasse.name());

		em.merge(persistenteKlasse);

		return mapFromDB(persistenteKlasse);
	}

	@Override
	public boolean removeKlasse(final Klasse klasse) {

		PersistenteKlasse persistenteKlasse = findPersistenteKlasse(klasse.identifier());

		if (persistenteKlasse == null) {

			return false;
		}

		em.remove(persistenteKlasse);
		return true;

	}

	private PersistenteKlasse findPersistenteKlasse(final Identifier klasseID) {

		PersistenteKlasse persistenteKlasse = em.find(PersistenteKlasse.class, klasseID.identifier());
		return persistenteKlasse;
	}

}
