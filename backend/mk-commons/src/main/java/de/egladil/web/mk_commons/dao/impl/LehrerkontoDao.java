// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.ILehrerkontoDao;

/**
 * LehrerkontoDao
 */
public class LehrerkontoDao extends BaseDao implements ILehrerkontoDao {

	/**
	 *
	 */
	public LehrerkontoDao() {

	}

	/**
	 * @param em
	 */
	public LehrerkontoDao(final EntityManager em) {

		super(em);

	}

}
