//=====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mkadmin_server.dao;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mkadmin_server.domain.MkadminEntity;

/**
 * BaseDao
 */
public interface BaseDao {

	/**
	 * Tja, was wohl.
	 *
	 * @param entity MkadminEntity
	 * @return MkadminEntity
	 */
	<T extends MkadminEntity> T save(T entity);

	/**
	 * Tja, was wohl.
	 *
	 * @param entity MkadminEntity
	 * @return MkadminEntity
	 */
	<T extends MkadminEntity> void delete(T entity);

	/**
	 * Sucht die Entity anhand ihres eindeutigen fachlichen Schlüssels.
	 *
	 * @param identifierValue String
	 * @return Optional
	 */
	<T extends MkadminEntity> Optional<T> findByUniqueIdentifier(String identifierValue);

	/**
	 * Läd die Entity anhand der technischen Id.
	 *
	 * @param id
	 * @return T oder null.
	 */
	<T extends MkadminEntity> T findById(Long id);

	/**
	 * Läd alle Entities.
	 *
	 * @return
	 */
	<T extends MkadminEntity> List<T> load();

	/**
	 *
	 * @return
	 */
	Integer getAnzahl();

}
