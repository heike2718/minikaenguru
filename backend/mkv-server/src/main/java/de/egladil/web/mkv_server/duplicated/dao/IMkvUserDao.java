// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.duplicated.dao;

import java.util.Optional;

import de.egladil.web.mk_commons.domain.impl.User;

/**
 * IMkvUserDao nach Fix von https://github.com/quarkusio/quarkus/issues/5015 ersetzen durch
 * IUserDao in mk-commons
 */
public interface IMkvUserDao {

	/**
	 * @param  uuid
	 *              String
	 * @return      Optional
	 */
	Optional<User> findByUUId(String uuid);

}
