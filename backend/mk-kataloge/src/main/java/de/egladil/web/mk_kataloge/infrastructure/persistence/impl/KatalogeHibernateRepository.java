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

import de.egladil.web.mk_kataloge.domain.KatalogeRepository;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Land;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * KatalogeHibernateRepository
 */
@RequestScoped
public class KatalogeHibernateRepository implements KatalogeRepository {

	private static final String UNBEKANNT = "Unbekannt";

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

		TypedQuery<Land> query = em.createNamedQuery(Land.QUERY_LOAD_LAENDER, Land.class).setParameter("excluded", UNBEKANNT);

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

		TypedQuery<Ort> query = em.createNamedQuery(Ort.QUERY_LOAD_ORTE_IN_LAND, Ort.class);
		query.setParameter("landKuerzel", landKuerzel).setParameter("excluded", UNBEKANNT);

		return query.getResultList();
	}

	@Override
	public List<Ort> findOrteInLand(final String landKuerzel, final String searchTerm) {

		TypedQuery<Ort> query = em.createQuery(Ort.QUERY_FIND_ORTE_IN_LAND, Ort.class);
		query.setParameter("name", searchTerm.toLowerCase() + "%").setParameter("landKuerzel", landKuerzel).setParameter("excluded",
			UNBEKANNT);

		return query.getResultList();
	}

	@Override
	public List<Land> findLander(final String searchTerm) {

		TypedQuery<Land> query = em.createNamedQuery(Land.QUERY_FIND_LAENDER_MIT_NAME, Land.class);
		query.setParameter("name", searchTerm.toLowerCase() + "%")
			.setParameter("excluded", UNBEKANNT);

		return getLaender(query);
	}

	@Override
	public List<Schule> findSchulen(final String searchTerm) {

		TypedQuery<Schule> query = em.createNamedQuery(Schule.QUERY_FIND_SCHULEN_MIT_NAME, Schule.class);
		query.setParameter("name", "%" + searchTerm.toLowerCase() + "%").setParameter("excluded", UNBEKANNT);

		return query.getResultList();
	}

	@Override
	public List<Ort> findOrte(final String searchTerm) {

		TypedQuery<Ort> query = em.createNamedQuery(Ort.QUERY_FIND_ORTE_MIT_NAME, Ort.class);
		query.setParameter("name", searchTerm.toLowerCase() + "%").setParameter("excluded", UNBEKANNT);

		return query.getResultList();
	}

	@Override
	public List<Schule> loadSchulenInOrt(final String ortKuerzel) {

		TypedQuery<Schule> query = em.createNamedQuery(Schule.QUERY_LOAD_SCHULEN_IN_ORT, Schule.class)
			.setParameter("ortKuerzel", ortKuerzel)
			.setParameter("excluded", UNBEKANNT);

		return query.getResultList();
	}

	public List<Schule> findSchulenInOrt(final String ortKuerzel, final String searchTerm) {

		TypedQuery<Schule> query = em.createNamedQuery(Schule.QUERY_FIND_SCHULEN_IN_ORT, Schule.class);
		query.setParameter("name", "%" + searchTerm.toLowerCase() + "%")
			.setParameter("ortKuerzel", ortKuerzel)
			.setParameter("excluded", UNBEKANNT);

		return query.getResultList();
	}

	@Override
	public Optional<Land> findLandWithKuerzel(final String landKuerzel) {

		TypedQuery<Land> query = em.createNamedQuery(Land.QUERY_FIND_BY_KUEZEL, Land.class).setParameter("kuerzel", landKuerzel);

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

		TypedQuery<Ort> query = em.createNamedQuery(Ort.QUERY_FIND_ORT_BY_KUERZEL, Ort.class).setParameter("kuerzel", ortKuerzel);

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

		TypedQuery<Schule> query = em.createNamedQuery(Schule.QUERY_FIND_BY_KUERZEL, Schule.class).setParameter("kuerzel",
			schulkuerzel);

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

		TypedQuery<Long> query = em.createNamedQuery(Schule.QUERY_COUNT_IN_ORT, Long.class)
			.setParameter("kuerzel", kuerzel.trim()).setParameter("excluded", UNBEKANNT);

		Long result = query.getSingleResult();

		return result.intValue();
	}

	@Override
	public int countOrteInLand(final String kuerzel) {

		TypedQuery<Long> query = em.createNamedQuery(Ort.QUERY_COUNT_IN_LAND, Long.class)
			.setParameter("kuerzel", kuerzel.trim()).setParameter("excluded", UNBEKANNT);

		Long result = query.getSingleResult();

		return result.intValue();
	}

	@Override
	public List<Schule> findSchulenWithKuerzeln(final List<String> schulkuerzel) {

		return em.createNamedQuery(Schule.QUERY_FIND_SCHULEN_WITH_KUERZELN, Schule.class).setParameter("kuerzeln", schulkuerzel)
			.getResultList();
	}

	@Override
	public int countSchulenMitKuerzel(final String kuerzel) {

		TypedQuery<Long> query = em.createNamedQuery(Schule.QUERY_COUNT_WITH_KUERZEL, Long.class).setParameter("kuerzel", kuerzel);

		Long result = query.getSingleResult();

		return result.intValue();
	}

	@Override
	public int countOrteMitKuerzel(final String kuerzel) {

		TypedQuery<Long> query = em.createNamedQuery(Ort.QUERY_COUNT_WITH_KUERZEL, Long.class).setParameter("kuerzel", kuerzel);

		Long result = query.getSingleResult();

		return result.intValue();
	}

	@Override
	public int countLaenderMitKuerzel(final String kuerzel) {

		TypedQuery<Long> query = em.createNamedQuery(Land.QUERY_COUNT_WITH_KUERZEL, Long.class).setParameter("kuerzel", kuerzel);

		Long result = query.getSingleResult();

		return result.intValue();
	}
}
