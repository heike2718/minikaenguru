//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IUserDao;

/**
* UserDao
*/
public class UserDao extends BaseDao implements IUserDao {

	/**
	 * 
	 */
	public UserDao() {

	}

	/**
	 * @param em
	 */
	public UserDao(EntityManager em) {

		super(em);

	}

}
