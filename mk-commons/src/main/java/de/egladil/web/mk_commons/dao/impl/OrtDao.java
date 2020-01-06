//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IOrtDao;

/**
* OrtDao
*/
public class OrtDao extends BaseDao implements IOrtDao {

	/**
	 * 
	 */
	public OrtDao() {

	}

	/**
	 * @param em
	 */
	public OrtDao(EntityManager em) {

		super(em);

	}

}
