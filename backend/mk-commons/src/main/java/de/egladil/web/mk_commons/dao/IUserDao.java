// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao;

import java.util.Optional;

import de.egladil.web.mk_commons.domain.impl.User;

/**
 * IUserDao
 */
public interface IUserDao extends IBaseDao {

	/**
	 * @param  uuid
	 *              String
	 * @return      Optional
	 */
	Optional<User> findByUUId(String uuid);

}
