// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IAdvVereinbarungDao;

/**
 * AdvVereinbarungDao
 */
public class AdvVereinbarungDao extends BaseDao implements IAdvVereinbarungDao {

	/**
	 *
	 */
	public AdvVereinbarungDao() {

	}

	/**
	 * @param em
	 */
	public AdvVereinbarungDao(final EntityManager em) {

		super(em);
	}

}
