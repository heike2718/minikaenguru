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
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_kataloge.domain.KatalogAPIException;
import de.egladil.web.mk_kataloge.domain.KatalogeRepository;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Land;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * KatalogeHibernateRepository
 */
@RequestScoped
public class KatalogeHibernateRepository implements KatalogeRepository {

	private static final Logger LOG = LoggerFactory.getLogger(KatalogeHibernateRepository.class);

	@Inject
	EntityManager em;

	/**
	 *
	 */
	public KatalogeHibernateRepository() {

	}

	@Override
	public List<Land> loadLaender() {

		String stmt = "select l from Land l";

		TypedQuery<Land> query = em.createQuery(stmt, Land.class);

		return getLaender(query);
	}

	/**
	 * @param  mapper
	 * @param  query
	 * @return
	 */
	private List<Land> getLaender(final TypedQuery<Land> query) {

		List<Land> trefferliste = query.getResultList();
		return trefferliste;
	}

	@Override
	public List<Ort> loadOrteInLand(final String landKuerzel) {

		String stmt = "select o from Ort o where o.landKuerzel = :landKuerzel";

		TypedQuery<Ort> query = em.createQuery(stmt, Ort.class);
		query.setParameter("landKuerzel", landKuerzel);

		return query.getResultList();
	}

	@Override
	public List<Ort> findOrteInLand(final String landKuerzel, final String searchTerm) {

		String stmt = "select o from Ort o where o.landKuerzel = :landKuerzel and lower(o.name) like :name";

		TypedQuery<Ort> query = em.createQuery(stmt, Ort.class);
		query.setParameter("name", searchTerm.toLowerCase() + "%");
		query.setParameter("landKuerzel", landKuerzel);

		return query.getResultList();
	}

	@Override
	public List<Land> findLander(final String searchTerm) {

		String stmt = "select l from Land l where lower(l.name) like :name";

		TypedQuery<Land> query = em.createQuery(stmt, Land.class);
		query.setParameter("name", searchTerm.toLowerCase() + "%");

		return getLaender(query);
	}

	@Override
	public List<Schule> findSchulen(final String searchTerm) {

		String stmt = "select s from Schule s where lower(s.name) like :name";

		TypedQuery<Schule> query = em.createQuery(stmt, Schule.class);
		query.setParameter("name", searchTerm.toLowerCase() + "%");

		return query.getResultList();
	}

	@Override
	public List<Ort> findOrte(final String searchTerm) {

		String stmt = "select o from Ort o where lower(o.name) like :name";

		TypedQuery<Ort> query = em.createQuery(stmt, Ort.class);
		query.setParameter("name", searchTerm.toLowerCase() + "%");

		return query.getResultList();
	}

	@Override
	public List<Schule> loadSchulenInOrt(final String ortKuerzel) {

		String stmt = "select s from Schule s where s.ortKuerzel = :ortKuerzel";

		TypedQuery<Schule> query = em.createQuery(stmt, Schule.class);
		query.setParameter("ortKuerzel", ortKuerzel);

		return query.getResultList();
	}

	public List<Schule> findSchulenInOrt(final String ortKuerzel, final String searchTerm) {

		String stmt = "select s from Schule s where s.ortKuerzel = :ortKuerzel and lower(s.name) like :name";

		TypedQuery<Schule> query = em.createQuery(stmt, Schule.class);
		query.setParameter("name", "%" + searchTerm.toLowerCase() + "%");
		query.setParameter("ortKuerzel", ortKuerzel);

		return query.getResultList();
	}

	@Override
	public Optional<Land> findLandWithKuerzel(final String landKuerzel) {

		String stmt = "select l from Land l where l.kuerzel = :kuerzel";
		TypedQuery<Land> query = em.createQuery(stmt, Land.class).setParameter("kuerzel", landKuerzel);

		try {

			final Land singleResult = query.getSingleResult();
			return Optional.of(singleResult);
		} catch (NoResultException e) {

			LOG.debug("nicht gefunden: {} - {}", "Land", landKuerzel);
			return Optional.empty();
		} catch (NonUniqueResultException e) {

			String msg = "Länder: Trefferliste zu '" + landKuerzel + "' nicht eindeutig";
			throw new KatalogAPIException(msg);

		} catch (PersistenceException e) {

			String msg = "Unerwarteter Fehler beim Suchen der Entity Land mit kuerzel="
				+ landKuerzel;
			LOG.error("{}: {}", e.getMessage(), e);
			throw new KatalogAPIException(msg);
		}
	}

	@Override
	public Optional<Ort> findOrtWithKuerzel(final String ortKuerzel) {

		String stmt = "select o from Ort o where o.kuerzel = :kuerzel";
		TypedQuery<Ort> query = em.createQuery(stmt, Ort.class).setParameter("kuerzel", ortKuerzel);

		try {

			final Ort singleResult = query.getSingleResult();
			return Optional.of(singleResult);
		} catch (NoResultException e) {

			LOG.debug("nicht gefunden: {} - {}", "Ort", ortKuerzel);
			return Optional.empty();
		} catch (NonUniqueResultException e) {

			String msg = "Orte: Trefferliste zu '" + ortKuerzel + "' nicht eindeutig";
			throw new KatalogAPIException(msg);

		} catch (PersistenceException e) {

			String msg = "Unerwarteter Fehler beim Suchen der Entity Ort mit kuerzel="
				+ ortKuerzel;
			LOG.error("{}: {}", e.getMessage(), e);
			throw new KatalogAPIException(msg);
		}
	}

	@Override
	public Optional<Schule> findSchuleWithKuerzel(final String schulkuerzel) {

		String stmt = "select s from Schule s where s.kuerzel = :kuerzel";
		TypedQuery<Schule> query = em.createQuery(stmt, Schule.class).setParameter("kuerzel", schulkuerzel);

		try {

			final Schule singleResult = query.getSingleResult();
			return Optional.of(singleResult);
		} catch (NoResultException e) {

			LOG.debug("nicht gefunden: {} - {}", "Schule", schulkuerzel);
			return Optional.empty();
		} catch (NonUniqueResultException e) {

			String msg = "Schulen: Trefferliste zu '" + schulkuerzel + "' nicht eindeutig";
			throw new KatalogAPIException(msg);

		} catch (PersistenceException e) {

			String msg = "Unerwarteter Fehler beim Suchen der Entity Schule mit kuerzel="
				+ schulkuerzel;
			LOG.error("{}: {}", e.getMessage(), e);
			throw new KatalogAPIException(msg);
		}
	}

	@Override
	public int countSchulenInOrt(final String kuerzel) {

		String stmt = "select count(s) from Schule s where s.ortKuerzel = :kuerzel";
		TypedQuery<Long> query = em.createQuery(stmt, Long.class)
			.setParameter("kuerzel", kuerzel.trim());

		Long result = query.getSingleResult();

		return result.intValue();
	}

	@Override
	public int countOrteInLand(final String kuerzel) {

		String stmt = "select count(o) from Ort o where o.landKuerzel = :kuerzel";
		TypedQuery<Long> query = em.createQuery(stmt, Long.class)
			.setParameter("kuerzel", kuerzel.trim());

		Long result = query.getSingleResult();

		return result.intValue();
	}

	@Override
	public List<Schule> findSchulenWithKuerzeln(final List<String> schulkuerzel) {

		String stmt = "select s from Schule s where s.kuerzel IN :kuerzeln";

		return em.createQuery(stmt, Schule.class).setParameter("kuerzeln", schulkuerzel).getResultList();
	}
}
