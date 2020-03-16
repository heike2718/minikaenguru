// =====================================================
// Project: mk-kataloge-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge_admin.persistence.impl;

import javax.enterprise.context.RequestScoped;

import de.egladil.web.mk_kataloge_admin.persistence.LandDao;
import de.egladil.web.mk_kataloge_admin.persistence.entities.KatalogeAdminEntity;
import de.egladil.web.mk_kataloge_admin.persistence.entities.Land;

/**
 * LandDaoImpl
 */
@RequestScoped
public class LandDaoImpl extends BaseDaoImpl implements LandDao {

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends KatalogeAdminEntity> Class<T> getEntityClass() {

		return (Class<T>) Land.class;
	}

}
