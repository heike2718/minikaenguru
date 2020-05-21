// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.PersistenterWettbewerb;

/**
 * WettbewerbHibernateRepository
 */
@RequestScoped
public class WettbewerbHibernateRepository implements WettbewerbRepository {

	@Inject
	EntityManager em;

	@Override
	public List<Wettbewerb> loadWettbewerbe() {

		List<PersistenterWettbewerb> persistenteWettbewerbe = em
			.createNamedQuery(PersistenterWettbewerb.LOAD_WETTBEWERBE_QUERY, PersistenterWettbewerb.class).getResultList();

		return persistenteWettbewerbe.stream().map(pw -> mapFromPersistenterWettbewerb(pw)).collect(Collectors.toList());
	}

	/**
	 * @param  persistenterWettbewerb
	 * @return
	 */
	private Wettbewerb mapFromPersistenterWettbewerb(final PersistenterWettbewerb persistenterWettbewerb) {

		Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(Integer.valueOf(persistenterWettbewerb.getUuid())))
			.withDatumFreischaltungLehrer(
				CommonTimeUtils.transformToLocalDateFromDate(persistenterWettbewerb.getDatumFreischaltungLehrer()))
			.withDatumFreischaltungPrivat(
				CommonTimeUtils.transformToLocalDateFromDate(persistenterWettbewerb.getDatumFreischaltungPrivat()))
			.withWettbewerbsbeginn(CommonTimeUtils.transformToLocalDateFromDate(persistenterWettbewerb.getWettbewerbsbeginn()))
			.withWettbewerbsende(CommonTimeUtils.transformToLocalDateFromDate(persistenterWettbewerb.getWettbewerbsende()))
			.withStatus(persistenterWettbewerb.getStatus());
		return wettbewerb;
	}

}
