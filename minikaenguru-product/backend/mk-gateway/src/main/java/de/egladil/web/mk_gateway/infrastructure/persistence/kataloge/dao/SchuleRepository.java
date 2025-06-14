// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.dao;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_gateway.domain.error.DuplicateEntityException;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Land;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Ort;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Schule;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * SchuleRepository
 */
@RequestScoped
public class SchuleRepository {

	@Inject
	@PersistenceUnit("kataloge")
	EntityManager em;

	@Inject
	KatalogeRepository katalogRepository;

	@Transactional
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

	@Transactional
	public void updateSchule(Schule schule) {
		this.em.merge(schule);
	}

	public Optional<Schule> getSchule(final String kuerzel) {

		List<Schule> trefferliste = em.createNamedQuery(Schule.FIND_BY_KUERZEL, Schule.class).setParameter("kuerzel", kuerzel)
			.getResultList();

		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));
	}

	public Optional<Ort> getOrt(final String kuerzel) {

		List<Ort> trefferliste = em.createNamedQuery(Ort.QUERY_FIND_ORT_BY_KUERZEL, Ort.class).setParameter("kuerzel", kuerzel)
			.getResultList();

		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));
	}

	public List<Schule> findSchulenInOrt(final String ortKuerzel) {

		return em.createNamedQuery(Schule.LOAD_SCHULEN_WITH_ORTKUERZEL, Schule.class).setParameter("ortKuerzel", ortKuerzel)
			.getResultList();
	}

	public List<Ort> findOrteInLand(final String landKuerzel) {

		return em.createNamedQuery(Ort.QUERY_LOAD_ORTE_WITH_LANDKUERZEL, Ort.class).setParameter("landKuerzel", landKuerzel)
			.getResultList();
	}

	public List<Schule> findSchulenInLand(final String landKuerzel) {

		return em.createNamedQuery(Schule.LOAD_SCHULEN_WITH_LANDKUERZEL, Schule.class).setParameter("landKuerzel", landKuerzel)
			.getResultList();
	}

	public List<Land> loadLaender() {

		return katalogRepository.loadLaender();
	}

	/**
	 * Da das Datenmodell ist, wie es ist, müssen die Schulen eines Ortes "umgehängt" werden, also deren Orte umbenannt.
	 *
	 * @param schulen
	 */
	@Transactional
	public void replaceSchulen(List<Schule> schulen) {

		for (Schule schule : schulen) {
			em.merge(schule);
		}
	}

}
