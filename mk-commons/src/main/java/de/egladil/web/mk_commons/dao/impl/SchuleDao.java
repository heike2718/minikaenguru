//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.ISchuleDao;

/**
* SchuleDao
*/
public class SchuleDao extends BaseDao implements ISchuleDao {

	/**
	 * 
	 */
	public SchuleDao() {

	}

	/**
	 * @param em
	 */
	public SchuleDao(EntityManager em) {

		super(em);

	}

}
