//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IWettbewerbsloesungDao;

/**
* WettbewerbsloesungDao
*/
public class WettbewerbsloesungDao extends BaseDao implements IWettbewerbsloesungDao {

	/**
	 * 
	 */
	public WettbewerbsloesungDao() {

	}

	/**
	 * @param em
	 */
	public WettbewerbsloesungDao(EntityManager em) {

		super(em);

	}

}
