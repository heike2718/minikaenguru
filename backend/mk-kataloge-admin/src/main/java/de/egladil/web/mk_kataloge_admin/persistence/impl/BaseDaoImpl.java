// =====================================================
// Project: mk-kataloge-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge_admin.persistence.impl;

import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_kataloge_admin.persistence.BaseDao;
import de.egladil.web.mk_kataloge_admin.persistence.entities.KatalogeAdminEntity;
import de.egladil.web.mk_kataloge_admin.persistence.error.KatalogeAdminRuntimeException;

/**
 * BaseDaoImpl
 */
public abstract class BaseDaoImpl implements BaseDao {

	private static final Logger LOG = LoggerFactory.getLogger(BaseDaoImpl.class);

	@Inject
	EntityManager em;

	@Override
	@Transactional(value = TxType.REQUIRED)
	public <T extends KatalogeAdminEntity> T save(final T entity) {

		T persisted;

		if (entity.getId() == null) {

			em.persist(entity);
			persisted = entity;
			LOG.debug("created: {}, ID={}", persisted, persisted.getId());
		} else {

			persisted = em.merge(entity);
			LOG.debug("updated: {}", persisted);
		}

		return persisted;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends KatalogeAdminEntity> Optional<T> findByKuerzel(final String kuerzel) {

		String stmt = "select e " + getClass().getSimpleName() + " e where e:kuerzel = :kuerzel";
		TypedQuery<KatalogeAdminEntity> query = em.createQuery(stmt, getEntityClass()).setParameter("kuerzel", kuerzel);

		try {

			final KatalogeAdminEntity singleResult = query.getSingleResult();
			return (Optional<T>) Optional.of(singleResult);
		} catch (NoResultException e) {

			LOG.debug("nicht gefunden: {} - {}", getEntityClass().getSimpleName(), kuerzel);
			return Optional.empty();
		} catch (NonUniqueResultException e) {

			String msg = getEntityClass().getSimpleName() + ": Trefferliste zu '" + kuerzel + "' nicht eindeutig";
			throw new KatalogeAdminRuntimeException(msg);

		} catch (PersistenceException e) {

			String msg = "Unerwarteter Fehler beim Suchen der Entity " + getEntityClass().getSimpleName() + " mit kuerzel="
				+ kuerzel;
			LOG.error("{}: {}", e.getMessage(), e);
			throw new KatalogeAdminRuntimeException(msg);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends KatalogeAdminEntity> T findById(final Long id) {

		return (T) em.find(getEntityClass(), id);
	}

	protected abstract <T extends KatalogeAdminEntity> Class<T> getEntityClass();

}
