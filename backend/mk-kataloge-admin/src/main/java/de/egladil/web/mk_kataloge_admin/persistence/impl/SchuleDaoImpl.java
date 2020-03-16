// =====================================================
// Project: mk-kataloge-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge_admin.persistence.impl;

import javax.enterprise.context.RequestScoped;

import de.egladil.web.mk_kataloge_admin.persistence.SchuleDao;
import de.egladil.web.mk_kataloge_admin.persistence.entities.KatalogeAdminEntity;
import de.egladil.web.mk_kataloge_admin.persistence.entities.Schule;

/**
 * SchuleDaoImpl
 */
@RequestScoped
public class SchuleDaoImpl extends BaseDaoImpl implements SchuleDao {

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends KatalogeAdminEntity> Class<T> getEntityClass() {

		return (Class<T>) Schule.class;
	}

}
