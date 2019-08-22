//=====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mkadmin_server.dao.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mkadmin_server.dao.BaseDao;
import de.egladil.web.mkadmin_server.domain.MkadminEntity;
import de.egladil.web.mkadmin_server.error.MkadminRuntimeException;

/**
 * BaseDaoImpl
 */
public abstract class BaseDaoImpl implements BaseDao {

	private static final Logger LOG = LoggerFactory.getLogger(BaseDaoImpl.class.getName());

	@PersistenceContext
	private EntityManager em;

	/**
	 *
	 */
	public BaseDaoImpl() {
	}

	/**
	 * @param em EntityManager
	 */
	public BaseDaoImpl(final EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public <T extends MkadminEntity> T save(final T entity) {

		T persisted = null;

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

	@Override
	@Transactional(value = TxType.REQUIRED)
	public <T extends MkadminEntity> void delete(final T entity) {
		getEm().remove(entity);
	}

	@Override
	public <T extends MkadminEntity> Optional<T> findByUniqueIdentifier(final String identifierValue) {

		Class<MkadminEntity> entityClass = getEntityClass();
		String stmt = "select e from " + entityClass.getSimpleName() + " e where e." + getNameFachlicherSchluessel()
			+ " = :identifier";

		TypedQuery<T> query = getEm().createQuery(stmt, getEntityClass());
		query.setParameter("identifier", identifierValue);

		try {
			final T singleResult = query.getSingleResult();
			LOG.debug("gefunden: {} - {}", entityClass.getSimpleName(), identifierValue);
			return Optional.of(singleResult);
		} catch (NoResultException e) {
			LOG.debug("nicht gefunden: {} - {}", entityClass.getSimpleName(), identifierValue);
			return Optional.empty();
		} catch (NonUniqueResultException e) {
			String msg = entityClass.getSimpleName() + ": Trefferliste zu '" + identifierValue + "' nicht eindeutig";
			throw new MkadminRuntimeException(msg);
		} catch (PersistenceException e) {
			String msg = "Unerwarteter Fehler beim Suchen der Entity " + entityClass.getSimpleName();
			LOG.error("{}: {}", e.getMessage(), e);
			throw new MkadminRuntimeException(msg);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MkadminEntity> T findById(final Long id) {
		return (T) getEm().find(getEntityClass(), id);
	}

	@Override
	public <T extends MkadminEntity> List<T> load() {

		final String entityName = getEntityClass().getSimpleName();
		String stmt = "select e from " + entityName + " e";

		TypedQuery<T> query = getEm().createQuery(stmt, getEntityClass());

		List<T> trefferliste = query.getResultList();

		LOG.debug("{} - Anzahl Treffer: {}", entityName, trefferliste.size());

		return trefferliste;

	}

	@Override
	public Integer getAnzahl() {

		String stmt = "select count(*) from " + getTableName();
		final Query query = getEm().createNativeQuery(stmt);

		return getCount(query).intValue();
	}

	private BigInteger getCount(final Query query) {

		final Object res = query.getSingleResult();

		if (!(res instanceof BigInteger)) {
			throw new MkadminRuntimeException("result ist kein BigInteger, sondern " + res.getClass());
		}

		return (BigInteger) res;
	}

	/**
	 * Gibt die Klasse zurück, die die gesuchte Entity ist.
	 *
	 * @return Class<T>
	 */
	protected abstract <T extends MkadminEntity> Class<T> getEntityClass();

	/**
	 * Der Name des Attributs, der für die Entity der fachliche Schlüssel ist.
	 *
	 * @return String
	 */
	public abstract String getNameFachlicherSchluessel();

	/**
	 * Returns the fully qualified Tablenam.
	 *
	 * @return String
	 */
	protected abstract String getTableName();

	protected EntityManager getEm() {
		return em;
	}

}
