//=====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_commons.dao.impl;

import javax.persistence.EntityManager;

import de.egladil.web.mk_commons.dao.IUploadInfoDao;

/**
* UploadInfoDao
*/
public class UploadInfoDao extends BaseDao implements IUploadInfoDao {

	/**
	 * 
	 */
	public UploadInfoDao() {

	}

	/**
	 * @param em
	 */
	public UploadInfoDao(EntityManager em) {

		super(em);

	}

}
