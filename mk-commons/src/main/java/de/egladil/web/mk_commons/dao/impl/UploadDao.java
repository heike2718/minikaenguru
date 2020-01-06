//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IUploadDao;

/**
* UploadDao
*/
public class UploadDao extends BaseDao implements IUploadDao {

	/**
	 * 
	 */
	public UploadDao() {

	}

	/**
	 * @param em
	 */
	public UploadDao(EntityManager em) {

		super(em);

	}

}
