// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_kataloge.domain.KatalogeRepository;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.error.DuplicateEntityException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Land;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * SchuleHibernateRepository
 */
@RequestScoped
public class SchuleHibernateRepository implements SchuleRepository {

	@Inject
	EntityManager em;

	@Inject
	KatalogeRepository katalogRepository;

	public static SchuleHibernateRepository createForIntegrationTests(final EntityManager entityManager) {

		SchuleHibernateRepository result = new SchuleHibernateRepository();
		result.em = entityManager;
		result.katalogRepository = KatalogeHibernateRepository.createForIntegrationTests(entityManager);
		return result;
	}

	@Transactional
	@Override
	public boolean addSchule(final Schule schule) {

		if (schule == null) {

			throw new NullPointerException("schule");
		}

		if (StringUtils.isNotBlank(schule.getKuerzel())) {

			throw new IllegalArgumentException("Schulen mit bekanntem Kürzel können nicht angelegt werden.");
		}

		if (StringUtils.isBlank(schule.getImportiertesKuerzel())) {

			throw new IllegalArgumentException("importiertesKuerzel ist blank: Schulen können nur importiert werden.");
		}

		Optional<Schule> optSchule = katalogRepository.findSchuleWithKuerzel(schule.getImportiertesKuerzel());

		if (optSchule.isPresent()) {

			throw new DuplicateEntityException("Eintrag mit kuerzel " + schule.getImportiertesKuerzel() + " existiert bereits.");
		}

		em.persist(schule);

		return true;
	}

	@Override
	@Transactional
	public boolean replaceSchulen(final List<Schule> schulen) {

		for (Schule schule : schulen) {

			em.merge(schule);
		}

		return true;
	}

	@Override
	public Optional<Schule> getSchule(final String kuerzel) {

		List<Schule> trefferliste = em.createNamedQuery(Schule.QUERY_FIND_BY_KUERZEL, Schule.class).setParameter("kuerzel", kuerzel)
			.getResultList();

		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));
	}

	@Override
	public Optional<Ort> getOrt(final String kuerzel) {

		List<Ort> trefferliste = em.createNamedQuery(Ort.QUERY_FIND_ORT_BY_KUERZEL, Ort.class).setParameter("kuerzel", kuerzel)
			.getResultList();

		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));
	}

	@Override
	public List<Schule> findSchulenInOrt(final String ortKuerzel) {

		return em.createNamedQuery(Schule.QUERY_LOAD_SCHULEN_WITH_ORTKUERZEL, Schule.class).setParameter("ortKuerzel", ortKuerzel)
			.getResultList();
	}

	@Override
	public List<Ort> findOrteInLand(final String landKuerzel) {

		return em.createNamedQuery(Ort.QUERY_LOAD_ORTE_WITH_LANDKUERZEL, Ort.class).setParameter("landKuerzel", landKuerzel)
			.getResultList();
	}

	@Override
	public List<Schule> findSchulenInLand(final String landKuerzel) {

		return em.createNamedQuery(Schule.QUERY_LOAD_SCHULEN_WITH_LANDKUERZEL, Schule.class)
			.setParameter("landKuerzel", landKuerzel)
			.getResultList();
	}

	@Override
	public List<Land> loadLaender() {

		return katalogRepository.loadLaender();
	}

}
