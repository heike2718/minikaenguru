// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import de.egladil.web.mk_commons.dao.IBaseDao;
import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.exception.ConcurrentUpdateException;
import de.egladil.web.mk_commons.exception.DuplicateEntityException;

/**
 * BaseDao
 */
public abstract class BaseDao implements IBaseDao {

	@Inject
	EntityManager em;

	/**
	 *
	 */
	public BaseDao() {

	}

	@Override
	public <T extends IMkEntity> T findById(final Class<T> clazz, final Long id) {

		return em.find(clazz, id);
	}

	@Override
	public <T extends IMkEntity> List<T> load(final Class<T> clazz) {

		final String statement = "SELECT e FROM " + clazz.getSimpleName() + " e";
		final List<T> resultList = em.createQuery(statement.toString(), clazz).getResultList();
		return resultList;

	}

	/**
	 * Erzeugt eine Instanz für einen Test ohne CDI.
	 *
	 * @param em
	 */
	public BaseDao(final EntityManager em) {

		this.em = em;
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public <T extends IMkEntity> T save(final T entity) throws DuplicateEntityException, ConcurrentUpdateException {

		T persisted;

		if (entity.getId() == null) {

			em.persist(entity);
			persisted = entity;
		} else {

			persisted = em.merge(entity);
		}
		return persisted;
	}

	@Override
	public <T extends IMkEntity> boolean delete(final T entity) {

		if (entity == null) {

			throw new IllegalArgumentException("entity null");
		}

		em.remove(em.contains(entity) ? entity : em.merge(entity));
		return true;
	}

	protected EntityManager getEm() {

		return em;
	}

}
