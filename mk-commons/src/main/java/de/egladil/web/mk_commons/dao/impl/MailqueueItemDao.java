//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IMailqueueItemDao;

/**
* MailqueueItemDao
*/
public class MailqueueItemDao extends BaseDao implements IMailqueueItemDao {

	/**
	 * 
	 */
	public MailqueueItemDao() {

	}

	/**
	 * @param em
	 */
	public MailqueueItemDao(EntityManager em) {

		super(em);

	}

}
