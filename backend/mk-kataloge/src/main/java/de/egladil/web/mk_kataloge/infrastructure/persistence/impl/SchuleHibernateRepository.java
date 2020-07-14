// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.impl;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_kataloge.domain.KatalogeRepository;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.error.DataInconsistencyException;
import de.egladil.web.mk_kataloge.domain.error.DuplicateEntityException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * SchuleHibernateRepository
 */
@RequestScoped
public class SchuleHibernateRepository implements SchuleRepository {

	@Inject
	EntityManager em;

	@Inject
	KatalogeRepository katalogRepository;

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
	public Schule updateSchule(final Schule schule) {

		if (schule == null) {

			throw new IllegalArgumentException("schule");
		}

		if (StringUtils.isBlank(schule.getKuerzel())) {

			throw new IllegalArgumentException("Schulen ohne Kürzel können nicht geändert werden.");
		}

		Optional<Schule> optSchule = katalogRepository.findSchuleWithKuerzel(schule.getKuerzel());

		Schule persistedSchule = optSchule.get();

		if (!schule.getOrtName().equals(persistedSchule.getOrtName())) {

			List<Schule> andereSchulen = this.findAllOtherSchulenWithOrtKuerzel(persistedSchule);

			for (Schule s : andereSchulen) {

				s.setOrtName(schule.getOrtName());
				em.merge(s);
			}
		}

		persistedSchule.setName(schule.getName());
		persistedSchule.setOrtName(schule.getOrtName());

		return em.merge(persistedSchule);
	}

	private List<Schule> findAllOtherSchulenWithOrtKuerzel(final Schule schule) {

		String stmt = "select s from Schule s where s.ortKuerzel=:ortKuerzel and s.kuerzel != :kuerzel";

		TypedQuery<Schule> query = em.createQuery(stmt, Schule.class).setParameter("kuerzel", schule.getKuerzel())
			.setParameter("ortKuerzel", schule.getOrtKuerzel());

		return query.getResultList();
	}

	@Override
	public Optional<Schule> findSchuleInOrtMitName(final String kuerzelOrt, final String name) {

		if (StringUtils.isBlank(kuerzelOrt)) {

			throw new IllegalArgumentException("kuerzelOrt darf nicht blank sein.");
		}

		if (StringUtils.isBlank(name)) {

			throw new IllegalArgumentException("name darf nicht blank sein.");
		}

		TypedQuery<Schule> query = em.createNamedQuery(Schule.QUERY_FIND_SCHULE_IN_ORT_MIT_NAME, Schule.class)
			.setParameter("ortKuerzel", kuerzelOrt).setParameter("name", name.trim().toLowerCase());

		List<Schule> trefferliste = query.getResultList();

		if (trefferliste.size() > 1) {

			Schule schule = trefferliste.get(0);
			throw new DataInconsistencyException("Es gibt mehr als eine Schule mit dem Namen '" + name + "' im Ort '" + kuerzelOrt
				+ "', '" + schule.getOrtName() + "', Land '" + schule.getLandName() + "'.");

		}

		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));
	}

}
