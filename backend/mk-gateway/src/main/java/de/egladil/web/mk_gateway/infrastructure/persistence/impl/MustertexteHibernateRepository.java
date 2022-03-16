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
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertext;
import de.egladil.web.mk_gateway.domain.mustertexte.MustertexteRepository;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertextkategorie;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterMustertext;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterMustertextShort;

/**
 * MustertexteHibernateRepository
 */
@RequestScoped
public class MustertexteHibernateRepository implements MustertexteRepository {

	@Inject
	EntityManager entityManager;

	public static MustertexteHibernateRepository createForIntegrationTests(final EntityManager entityManager) {

		MustertexteHibernateRepository result = new MustertexteHibernateRepository();
		result.entityManager = entityManager;
		return result;
	}

	@Override
	public List<Mustertext> loadMustertexteByKategorie(final Mustertextkategorie kategorie) {

		List<PersistenterMustertextShort> trefferliste = entityManager
			.createNamedQuery(PersistenterMustertextShort.FIND_BY_KATEGORIE, PersistenterMustertextShort.class)
			.setParameter("kategorie", kategorie).getResultList();

		if (trefferliste.isEmpty()) {

			return new ArrayList<>();
		}

		return trefferliste.stream().map(pm -> mapFromDB(pm)).collect(Collectors.toList());
	}

	@Override
	public Optional<Mustertext> findMustertextByIdentifier(final Identifier identifier) {

		PersistenterMustertext persistenterMustertext = internalFind(identifier);
		return persistenterMustertext == null ? Optional.empty() : Optional.of(mapFromDB(persistenterMustertext));
	}

	@Override
	@Transactional
	public Mustertext addOrUpdate(final Mustertext mustertext) {

		PersistenterMustertext persistenterMustertext = internalFind(mustertext.getIdentifier());

		if (persistenterMustertext == null) {

			throw new MkGatewayRuntimeException("Mustertext mit UUID = " + mustertext.getIdentifier() + " aus DB verschwunden");
		}

		persistenterMustertext.setKategorie(mustertext.getKategorie());
		persistenterMustertext.setName(mustertext.getName());
		persistenterMustertext.setText(mustertext.getText());

		if (persistenterMustertext.getUuid() == null) {

			entityManager.persist(persistenterMustertext);
		} else {

			entityManager.merge(persistenterMustertext);
		}

		return mapFromDB(persistenterMustertext);
	}

	/**
	 * @param  mustertext
	 * @return
	 */
	private PersistenterMustertext internalFind(final Identifier identifier) {

		PersistenterMustertext persistenterMustertext = null;

		if (identifier != null) {

			return persistenterMustertext = entityManager.find(PersistenterMustertext.class, identifier.identifier());

		} else {

			persistenterMustertext = new PersistenterMustertext();
		}
		return persistenterMustertext;
	}

	@Override
	@Transactional
	public boolean deleteMustertext(final Identifier identifier) {

		PersistenterMustertext persistenter = entityManager.find(PersistenterMustertext.class, identifier.toString());

		if (persistenter != null) {

			entityManager.remove(persistenter);
			return true;
		}

		return false;

	}

	private Mustertext mapFromDB(final PersistenterMustertext source) {

		Mustertext result = new Mustertext(new Identifier(source.getUuid())).withKategorie(source.getKategorie())
			.withName(source.getName()).withText(source.getText());
		return result;
	}

	private Mustertext mapFromDB(final PersistenterMustertextShort source) {

		Mustertext result = new Mustertext(new Identifier(source.getUuid())).withKategorie(source.getKategorie())
			.withName(source.getName());
		return result;
	}

}
