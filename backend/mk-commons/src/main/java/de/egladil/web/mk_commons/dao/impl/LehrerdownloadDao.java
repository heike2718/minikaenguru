// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.ILehrerdodwnloadDao;

/**
 * LehrerdownloadDao
 */
public class LehrerdownloadDao extends BaseDao implements ILehrerdodwnloadDao {

	/**
	 *
	 */
	public LehrerdownloadDao() {

	}

	/**
	 * @param em
	 */
	public LehrerdownloadDao(final EntityManager em) {

		super(em);

	}

}
