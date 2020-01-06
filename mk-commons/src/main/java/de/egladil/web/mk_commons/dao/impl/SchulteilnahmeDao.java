//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.ISchulteilnahmeDao;

/**
* SchulteilnahmeDao
*/
public class SchulteilnahmeDao extends BaseDao implements ISchulteilnahmeDao {

	/**
	 * 
	 */
	public SchulteilnahmeDao() {

	}

	/**
	 * @param em
	 */
	public SchulteilnahmeDao(EntityManager em) {

		super(em);

	}

}
