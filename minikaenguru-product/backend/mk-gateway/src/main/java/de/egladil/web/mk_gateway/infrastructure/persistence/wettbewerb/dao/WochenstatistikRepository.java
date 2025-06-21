//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.dao;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.KalenderwochenIntervall;
import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.NativeQueriesEntity;
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

	@ConfigProperty(name = "aufsetzjahr.wochenstatistik")
	private int aufsetzjahrWochenstatistik;

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
			.setParameter("jahr", wettbewerbUUID).getResultList();
	}

	/**
	 * Selektiert das maximale Intervall an Kalenderwochen, für die über alle Jahre Lösungszettel existieren. Dies ist
	 * die Vorgabe für die x-Achse in der Wochenstatistik der Lösungszettel.
	 *
	 * @return KalenderwochenIntervall
	 */
	public KalenderwochenIntervall selectKalenderwochenIntervall() {

		List<KalenderwochenIntervall> resultList = entityManager
			.createNamedQuery(NativeQueriesEntity.SELECT_KALENDERWOCHEN_INTERVALL_LOESUNGSZETTEL, KalenderwochenIntervall.class)
			.setParameter("jahr", aufsetzjahrWochenstatistik)
			.getResultList();

		return resultList.isEmpty() ? null : resultList.get(0);
	}
}
