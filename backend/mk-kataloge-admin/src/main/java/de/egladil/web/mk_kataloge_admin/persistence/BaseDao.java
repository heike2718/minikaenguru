// =====================================================
// Project: mk-kataloge-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge_admin.persistence;

import java.util.Optional;

import de.egladil.web.mk_kataloge_admin.persistence.entities.KatalogeAdminEntity;

/**
 * BaseDao
 */
public interface BaseDao {

	/**
	 * Tja, was wohl.
	 *
	 * @param  entity
	 *                Checklistenentity
	 * @return        Checklistenentity
	 */
	<T extends KatalogeAdminEntity> T save(T entity);

	/**
	 * Sucht die Entity anhand ihres kuerzels.
	 *
	 * @param  kuerzel
	 *                 String
	 * @return         Optional
	 */
	<T extends KatalogeAdminEntity> Optional<T> findByKuerzel(String kuerzel);

	/**
	 * Läd die Entity anhand der technischen Id.
	 *
	 * @param  id
	 * @return    T oder null.
	 */
	<T extends KatalogeAdminEntity> T findById(Long id);
}
