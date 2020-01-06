// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.ILandDao;

/**
 * LandDao
 */
public class LandDao extends BaseDao implements ILandDao {

	/**
	 *
	 */
	public LandDao() {

	}

	/**
	 * @param em
	 */
	public LandDao(final EntityManager em) {

		super(em);

	}

}
