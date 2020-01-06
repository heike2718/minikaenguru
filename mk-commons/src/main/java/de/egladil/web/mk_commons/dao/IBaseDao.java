// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao;

import java.util.List;

import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.exception.ConcurrentUpdateException;
import de.egladil.web.mk_commons.exception.DuplicateEntityException;

/**
 * IBaseDao
 */
public interface IBaseDao {

	/**
	 * Tja, eben.
	 *
	 * @param  <T>
	 *               IMkEntity
	 * @param  clazz
	 *               Class
	 * @param  id
	 *               Long
	 * @return       T oder null
	 */
	<T extends IMkEntity> T findById(Class<T> clazz, Long id);

	/**
	 * Tja, eben.
	 *
	 * @param  <T>
	 *               IMkEntity
	 * @param  clazz
	 *               Class
	 * @param  id
	 *               Long
	 * @return       List of T
	 */
	<T extends IMkEntity> List<T> load(Class<T> clazz);

	/**
	 * Tja, eben.
	 *
	 * @param  <T>
	 *                                   IMkEntity
	 * @param  entity
	 *                                   T
	 * @return                           T
	 * @throws DuplicateEntityException
	 * @throws ConcurrentUpdateException
	 */
	<T extends IMkEntity> T save(final T entity) throws DuplicateEntityException, ConcurrentUpdateException;

	/**
	 * Löscht die gegebene Entity.<br>
	 * <br>
	 * <strong>Achtung: </strong> Der Aufrufer muss die Transaction markieren!!!! Es wird nichts geloggt. Das sollte die aufrufende
	 * Klasse tun.
	 *
	 * @param  <T>
	 * @param  entity
	 * @return        boolean true, wenn gelöscht, sonst Exception zwecks Mockito
	 */
	<T extends IMkEntity> boolean delete(final T entity);

}
