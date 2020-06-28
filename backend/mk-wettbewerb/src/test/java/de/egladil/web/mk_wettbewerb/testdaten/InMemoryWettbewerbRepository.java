// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.testdaten;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbStatus;

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
	public Optional<Wettbewerb> loadWettbewerbWithMaxJahr() {

		final Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(wettbewerbsjahr)).withStatus(status)
			.withWettbewerbsbeginn(LocalDate.of(wettbewerbsjahr, Month.JANUARY, 1))
			.withDatumFreischaltungLehrer(LocalDate.of(wettbewerbsjahr, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(wettbewerbsjahr, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(wettbewerbsjahr, Month.AUGUST, 1));
		return Optional.of(wettbewerb);
	}

}
