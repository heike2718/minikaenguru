//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IPacemakerDao;

/**
* PacemakerDao
*/
public class PacemakerDao extends BaseDao implements IPacemakerDao {

	/**
	 * 
	 */
	public PacemakerDao() {

	}

	/**
	 * @param em
	 */
	public PacemakerDao(EntityManager em) {

		super(em);

	}

}
