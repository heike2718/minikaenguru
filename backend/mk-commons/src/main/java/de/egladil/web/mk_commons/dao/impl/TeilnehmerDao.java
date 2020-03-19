//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.ITeilnehmerDao;

/**
* TeilnehmerDao
*/
public class TeilnehmerDao extends BaseDao implements ITeilnehmerDao {

	/**
	 * 
	 */
	public TeilnehmerDao() {

	}

	/**
	 * @param em
	 */
	public TeilnehmerDao(EntityManager em) {

		super(em);

	}

}
