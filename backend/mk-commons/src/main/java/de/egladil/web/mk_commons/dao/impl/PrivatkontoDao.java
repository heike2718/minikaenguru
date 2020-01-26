//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IPrivatkontoDao;

/**
* PrivatkontoDao
*/
public class PrivatkontoDao extends BaseDao implements IPrivatkontoDao {

	/**
	 * 
	 */
	public PrivatkontoDao() {

	}

	/**
	 * @param em
	 */
	public PrivatkontoDao(EntityManager em) {

		super(em);

	}

}
