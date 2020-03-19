// =====================================================
// Project: mk-kataloge-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge_admin.persistence.impl;

import javax.enterprise.context.RequestScoped;

import de.egladil.web.mk_kataloge_admin.persistence.OrtDao;
import de.egladil.web.mk_kataloge_admin.persistence.entities.KatalogeAdminEntity;
import de.egladil.web.mk_kataloge_admin.persistence.entities.Ort;

/**
 * OrtDaoImpl
 */
@RequestScoped
public class OrtDaoImpl extends BaseDaoImpl implements OrtDao {

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends KatalogeAdminEntity> Class<T> getEntityClass() {

		return (Class<T>) Ort.class;
	}

}
