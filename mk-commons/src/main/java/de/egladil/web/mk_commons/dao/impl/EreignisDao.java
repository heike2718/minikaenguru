// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IEreignisDao;

/**
 * EreignisDao
 */
public class EreignisDao extends BaseDao implements IEreignisDao {

	/**
	 *
	 */
	public EreignisDao() {

	}

	/**
	 * @param em
	 */
	public EreignisDao(final EntityManager em) {

		super(em);

	}

}
