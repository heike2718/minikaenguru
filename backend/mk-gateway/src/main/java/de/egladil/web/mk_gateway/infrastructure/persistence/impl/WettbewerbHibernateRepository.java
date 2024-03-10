// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterWettbewerb;

/**
 * WettbewerbHibernateRepository
 */
@ApplicationScoped
public class WettbewerbHibernateRepository implements WettbewerbRepository {

	@Inject
	EntityManager em;

	public static WettbewerbHibernateRepository createForIntegrationTest(final EntityManager em) {

		WettbewerbHibernateRepository result = new WettbewerbHibernateRepository();
		result.em = em;
		return result;
	}

	@Override
	public List<Wettbewerb> loadWettbewerbe() {

		List<PersistenterWettbewerb> persistenteWettbewerbe = em
			.createNamedQuery(PersistenterWettbewerb.LOAD_WETTBEWERBE_QUERY, PersistenterWettbewerb.class).getResultList();

		return persistenteWettbewerbe.stream().map(pw -> mapFromPersistenterWettbewerb(pw)).collect(Collectors.toList());
	}

	@Override
	public Optional<Wettbewerb> wettbewerbMitID(final WettbewerbID wettbewerbID) {

		Optional<PersistenterWettbewerb> opt = this.findPersistentenWetbewerbByUUID(wettbewerbID.toString());

		if (opt.isEmpty()) {

			return Optional.empty();
		}

		PersistenterWettbewerb persistenterWettbewerb = opt.get();
		Wettbewerb wettbewerb = mapFromPersistenterWettbewerb(persistenterWettbewerb);

		return Optional.of(wettbewerb);
	}

	/**
	 * @param  persistenterWettbewerb
	 * @return
	 */
	Wettbewerb mapFromPersistenterWettbewerb(final PersistenterWettbewerb persistenterWettbewerb) {

		Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(Integer.valueOf(persistenterWettbewerb.getUuid())))
			.withDatumFreischaltungLehrer(
				CommonTimeUtils.transformToLocalDateFromDate(persistenterWettbewerb.getDatumFreischaltungLehrer()))
			.withDatumFreischaltungPrivat(
				CommonTimeUtils.transformToLocalDateFromDate(persistenterWettbewerb.getDatumFreischaltungPrivat()))
			.withWettbewerbsbeginn(CommonTimeUtils.transformToLocalDateFromDate(persistenterWettbewerb.getWettbewerbsbeginn()))
			.withWettbewerbsende(CommonTimeUtils.transformToLocalDateFromDate(persistenterWettbewerb.getWettbewerbsende()))
			.withStatus(persistenterWettbewerb.getStatus())
			.withLoesungsbuchstabenIKids(persistenterWettbewerb.getLoesungsbuchstabenIkids())
			.withLoesungsbuchstabenKlasse1(persistenterWettbewerb.getLoesungsbuchstabenKlasse1())
			.withLoesungsbuchstabenKlasse2(persistenterWettbewerb.getLoesungsbuchstabenKlasse2());
		return wettbewerb;
	}

	private Optional<PersistenterWettbewerb> findPersistentenWetbewerbByUUID(final String uuid) {

		TypedQuery<PersistenterWettbewerb> query = em
			.createNamedQuery(PersistenterWettbewerb.FIND_WETTBEWERB_BY_ID_QUERY, PersistenterWettbewerb.class)
			.setParameter("uuid", uuid);

		List<PersistenterWettbewerb> trefferliste = query.getResultList();

		if (trefferliste.isEmpty()) {

			return Optional.empty();
		}

		return Optional.of(trefferliste.get(0));
	}

	@Override
	@Transactional
	public void addWettbewerb(final Wettbewerb wettbewerb) {

		PersistenterWettbewerb persistenterWettbewerb = new PersistenterWettbewerb();
		mapAllAttributesButStatus(wettbewerb, persistenterWettbewerb);
		persistenterWettbewerb.setStatus(wettbewerb.status());

		persistenterWettbewerb.setImportierteUuid(wettbewerb.id().toString());

		em.persist(persistenterWettbewerb);
	}

	@Override
	@Transactional
	public void changeWettbewerb(final Wettbewerb wettbewerb) {

		Optional<PersistenterWettbewerb> opt = this.findPersistentenWetbewerbByUUID(wettbewerb.id().toString());

		if (opt.isEmpty()) {

			throw new IllegalStateException("Den Wettbewerb " + wettbewerb.toString() + " gibt es noch nicht");
		}

		PersistenterWettbewerb persistenterWettbewerb = opt.get();
		this.mapAllAttributesButStatus(wettbewerb, persistenterWettbewerb);

		em.merge(persistenterWettbewerb);

	}

	@Override
	@Transactional
	public boolean changeWettbewerbStatus(final WettbewerbID wettbewerbId, final WettbewerbStatus neuerStatus) {

		Optional<PersistenterWettbewerb> opt = this.findPersistentenWetbewerbByUUID(wettbewerbId.toString());

		if (opt.isEmpty()) {

			throw new MkGatewayRuntimeException("Den Wettbewerb " + wettbewerbId.toString() + " gibt es noch nicht");
		}

		PersistenterWettbewerb persistenterWettbewerb = opt.get();
		persistenterWettbewerb.setStatus(neuerStatus);
		em.merge(persistenterWettbewerb);
		return true;
	}

	/**
	 * @param wettbewerb
	 * @param persistenterWettbewerb
	 */
	void mapAllAttributesButStatus(final Wettbewerb wettbewerb, final PersistenterWettbewerb persistenterWettbewerb) {

		persistenterWettbewerb
			.setDatumFreischaltungLehrer(CommonTimeUtils.transformFromLocalDate(wettbewerb.datumFreischaltungLehrer()));
		persistenterWettbewerb
			.setDatumFreischaltungPrivat(CommonTimeUtils.transformFromLocalDate(wettbewerb.datumFreischaltungPrivat()));
		persistenterWettbewerb.setWettbewerbsbeginn(CommonTimeUtils.transformFromLocalDate(wettbewerb.wettbewerbsbeginn()));
		persistenterWettbewerb.setWettbewerbsende(CommonTimeUtils.transformFromLocalDate(wettbewerb.wettbewerbsende()));
		persistenterWettbewerb.setLoesungsbuchstabenIkids(wettbewerb.loesungsbuchstabenIkids());
		persistenterWettbewerb.setLoesungsbuchstabenKlasse1(wettbewerb.loesungsbuchstabenKlasse1());
		persistenterWettbewerb.setLoesungsbuchstabenKlasse2(wettbewerb.loesungsbuchstabenKlasse2());
	}
}
