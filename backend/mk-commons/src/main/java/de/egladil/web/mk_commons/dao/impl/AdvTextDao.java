// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IAdvTextDao;

/**
 * AdvTextDao
 */
public class AdvTextDao extends BaseDao implements IAdvTextDao {

	/**
	 *
	 */
	public AdvTextDao() {

	}

	/**
	 * @param em
	 */
	public AdvTextDao(final EntityManager em) {

		super(em);
	}

}
