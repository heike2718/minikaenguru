// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.adv.Vertragstext;
import de.egladil.web.mk_gateway.domain.adv.VertragstextRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterVertragAdvText;

/**
 * VertragstextHibernateRepository
 */
@RequestScoped
public class VertragstextHibernateRepository implements VertragstextRepository {

	@Inject
	EntityManager em;

	@Override
	public Optional<Vertragstext> ofIdentifier(final Identifier identifier) {

		PersistenterVertragAdvText persistenterText = em.find(PersistenterVertragAdvText.class, identifier.identifier());

		if (persistenterText == null) {

			return Optional.empty();
		}

		return Optional.of(mapFromDb(persistenterText));
	}

	@Override
	public List<Vertragstext> loadVertragstexte() {

		List<PersistenterVertragAdvText> trefferliste = em
			.createNamedQuery(PersistenterVertragAdvText.LOAD, PersistenterVertragAdvText.class).getResultList();

		return trefferliste.stream().map(pt -> mapFromDb(pt)).collect(Collectors.toList());
	}

	Vertragstext mapFromDb(final PersistenterVertragAdvText persistenterVertragstext) {

		Vertragstext result = new Vertragstext().withChecksumme(persistenterVertragstext.getChecksumme())
			.withDateiname(persistenterVertragstext.getDateiname())
			.withIdentifier(new Identifier(persistenterVertragstext.getUuid()))
			.withVersionsnummer(persistenterVertragstext.getVersionsnummer());

		return result;

	}

}
