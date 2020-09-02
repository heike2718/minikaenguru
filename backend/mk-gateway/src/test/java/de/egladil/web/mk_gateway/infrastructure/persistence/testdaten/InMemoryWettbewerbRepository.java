// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;

/**
 * InMemoryWettbewerbRepository
 */
public class InMemoryWettbewerbRepository implements WettbewerbRepository {

	private final Integer wettbewerbsjahr;

	private final WettbewerbStatus status;

	public InMemoryWettbewerbRepository(final Integer jahr, final WettbewerbStatus status) {

		wettbewerbsjahr = jahr;
		this.status = status;
	}

	@Override
	public Optional<Wettbewerb> wettbewerbMitID(final WettbewerbID wettbewerbID) {

		return null;
	}

	@Override
	public void addWettbewerb(final Wettbewerb wettbewerb) {

	}

	@Override
	public void changeWettbewerb(final Wettbewerb wettbewerb) {

	}

	@Override
	public boolean changeWettbewerbStatus(final WettbewerbID wettbewerbId, final WettbewerbStatus neuerStatus) {

		return false;
	}

	@Override
	public List<Wettbewerb> loadWettbewerbe() {

		final Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(wettbewerbsjahr)).withStatus(status)
			.withWettbewerbsbeginn(LocalDate.of(wettbewerbsjahr, Month.JANUARY, 1))
			.withDatumFreischaltungLehrer(LocalDate.of(wettbewerbsjahr, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(wettbewerbsjahr, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(wettbewerbsjahr, Month.AUGUST, 1));

		return Arrays.asList(new Wettbewerb[] { wettbewerb });
	}

}
