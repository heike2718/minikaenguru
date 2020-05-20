// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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
	private Wettbewerb mapFromPersistenterWettbewerb(final PersistenterWettbewerb persistenterWettbewerb) {

		Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(Integer.valueOf(persistenterWettbewerb.getUuid())))
			.withDatumFreischaltungLehrer(transform(persistenterWettbewerb.getDatumFreischaltungLehrer()))
			.withDatumFreischaltungPrivat(transform(persistenterWettbewerb.getDatumFreischaltungPrivat()))
			.withWettbewerbsbeginn(transform(persistenterWettbewerb.getWettbewerbsbeginn()))
			.withWettbewerbsende(transform(persistenterWettbewerb.getWettbewerbsende()))
			.withStatus(persistenterWettbewerb.getStatus());
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
	public void addWettbewerb(final Wettbewerb wettbewerb) {

		Optional<PersistenterWettbewerb> opt = this.findPersistentenWetbewerbByUUID(wettbewerb.id().toString());

		if (opt.isPresent()) {

			throw new IllegalStateException("Den Wettbewerb " + wettbewerb.toString() + " gibt es schon");
		}

		PersistenterWettbewerb persistenterWettbewerb = opt.get();
		mapFromWettbewerb(wettbewerb, persistenterWettbewerb);

		em.merge(persistenterWettbewerb);

	}

	@Override
	public void changeWettbewerb(final Wettbewerb wettbewerb) {

		Optional<Wettbewerb> opt = this.wettbewerbMitID(wettbewerb.id());

		if (opt.isEmpty()) {

			throw new IllegalStateException("Den Wettbewerb " + wettbewerb.toString() + " gibt es noch nicjt");
		}

	}

	/**
	 * @param wettbewerb
	 * @param persistenterWettbewerb
	 */
	private void mapFromWettbewerb(final Wettbewerb wettbewerb, final PersistenterWettbewerb persistenterWettbewerb) {

		persistenterWettbewerb.setDatumFreischaltungLehrer(transform(wettbewerb.datumFreischaltungLehrer()));
		persistenterWettbewerb.setDatumFreischaltungPrivat(transform(wettbewerb.datumFreischaltungPrivat()));
		persistenterWettbewerb.setStatus(wettbewerb.status());
		persistenterWettbewerb.setWettbewerbsbeginn(transform(wettbewerb.wettbewerbsbeginn()));
		persistenterWettbewerb.setWettbewerbsende(transform(wettbewerb.wettbewerbsende()));
	}

	private LocalDate transform(final Date date) {

		return date == null ? null : LocalDate.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
	}

	private Date transform(final LocalDate localDate) {

		return localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

	}

}
