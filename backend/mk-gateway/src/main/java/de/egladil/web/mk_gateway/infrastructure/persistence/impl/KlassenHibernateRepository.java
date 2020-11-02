// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
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

		Klasse klasse = new Klasse(new Identifier(persistenteKlasse.getUuid())).withName(persistenteKlasse.getName())
			.withSchuleID(new Identifier(persistenteKlasse.getSchulkuerzel()));

		return Optional.of(klasse);
	}

}
