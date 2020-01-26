//=====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mkadmin_server.dao.impl;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

import de.egladil.web.mkadmin_server.dao.PacemakerDao;
import de.egladil.web.mkadmin_server.domain.MkadminEntity;
import de.egladil.web.mkadmin_server.domain.Pacemaker;

/**
 * PacemakerDaoImpl
 */
@RequestScoped
public class PacemakerDaoImpl extends BaseDaoImpl implements PacemakerDao {

	/**
	 *
	 */
	public PacemakerDaoImpl() {
	}

	/**
	 * @param em
	 */
	public PacemakerDaoImpl(final EntityManager em) {
		super(em);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends MkadminEntity> Class<T> getEntityClass() {
		return (Class<T>) Pacemaker.class;
	}

	@Override
	public String getNameFachlicherSchluessel() {
		return "monitorId";
	}

	@Override
	protected String getTableName() {
		return "PACEMAKER";
	}

}
