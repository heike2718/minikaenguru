// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IAuswertungDownloadDao;

/**
 * AuswertungDownloadDao
 */
public class AuswertungDownloadDao extends BaseDao implements IAuswertungDownloadDao {

	/**
	 *
	 */
	public AuswertungDownloadDao() {

	}

	/**
	 * @param em
	 */
	public AuswertungDownloadDao(final EntityManager em) {

		super(em);

	}

}
