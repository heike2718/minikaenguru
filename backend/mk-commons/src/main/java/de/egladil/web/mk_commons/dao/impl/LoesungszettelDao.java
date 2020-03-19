// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.ILoesungszettelDao;

/**
 * LoesungszettelDao
 */
public class LoesungszettelDao extends BaseDao implements ILoesungszettelDao {

	/**
	 *
	 */
	public LoesungszettelDao() {

	}

	/**
	 * @param em
	 */
	public LoesungszettelDao(final EntityManager em) {

		super(em);

	}

}
