//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.dao;

import java.util.List;

import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.WochenstatistikItem;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

/**
 *
 */
@RequestScoped
public class WochenstatistikRepository {

	@Inject
	@PersistenceUnit("wettbewerb")
	EntityManager entityManager;

	/**
	 *
	 * @param wettbewerbUUID
	 * @return List
	 */
	public List<WochenstatistikItem> loadWochenstatistiken(String wettbewerbUUID) {

		return entityManager.createNamedQuery(WochenstatistikItem.FIND_WITH_WETTBEWERB_UUID, WochenstatistikItem.class)
			.setParameter("wettbewerbUUID", wettbewerbUUID).getResultList();

	}

}
