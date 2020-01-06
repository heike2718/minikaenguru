//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IPrivatdownloadDao;

/**
* PrivatdownloadDao
*/
public class PrivatdownloadDao extends BaseDao implements IPrivatdownloadDao {

	/**
	 * 
	 */
	public PrivatdownloadDao() {

	}

	/**
	 * @param em
	 */
	public PrivatdownloadDao(EntityManager em) {

		super(em);

	}

}
