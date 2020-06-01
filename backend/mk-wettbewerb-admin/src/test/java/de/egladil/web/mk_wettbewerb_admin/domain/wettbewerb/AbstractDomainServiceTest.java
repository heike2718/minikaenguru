// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

/**
 * AbstractDomainServiceTest
 */
public abstract class AbstractDomainServiceTest {

	private WettbewerbRepository wettbewerbRepository;

	private int countWettbewerbInsert = 0;

	private int countWettbewerbUpdate = 0;

	private int countChangeWettbewerbStatus;

	protected void setUp() {

		List<Wettbewerb> wettbewerbe = new ArrayList<>();
		List<Integer> jahre = Arrays.asList(new Integer[] { 2005, 2017, 2010, 2015 });

		jahre.forEach(jahr -> {

			if (jahr.equals(2005)) {

				wettbewerbe.add(new Wettbewerb(new WettbewerbID(jahr)).withStatus(WettbewerbStatus.BEENDET)
					.withWettbewerbsbeginn(LocalDate.of(jahr, Month.JANUARY, 1))
					.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
					.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
					.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1)));
			}

			if (jahr.equals(2010)) {

				wettbewerbe.add(new Wettbewerb(new WettbewerbID(jahr)).withStatus(WettbewerbStatus.BEENDET)
					.withWettbewerbsbeginn(LocalDate.of(jahr, Month.JANUARY, 1))
					.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
					.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
					.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1)));

			}

			if (jahr.equals(2015)) {

				wettbewerbe.add(new Wettbewerb(new WettbewerbID(jahr)).withStatus(WettbewerbStatus.DOWNLOAD_PRIVAT)
					.withWettbewerbsbeginn(LocalDate.of(jahr, Month.JANUARY, 1))
					.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
					.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
					.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1)));

			}

			if (jahr.equals(2017)) {

				wettbewerbe.add(new Wettbewerb(new WettbewerbID(jahr)).withStatus(WettbewerbStatus.ERFASST)
					.withWettbewerbsbeginn(LocalDate.of(jahr, Month.JANUARY, 1))
					.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
					.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
					.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1)));

			}

		});

		wettbewerbRepository = new WettbewerbRepository() {

			@Override
			public Optional<Wettbewerb> wettbewerbMitID(final WettbewerbID wettbewerbID) {

				return wettbewerbe.stream().filter(w -> w.id().equals(wettbewerbID)).findFirst();
			}

			@Override
			public List<Wettbewerb> loadWettbewerbe() {

				return wettbewerbe;
			}

			@Override
			public boolean changeWettbewerbStatus(final WettbewerbID wettbewerbId, final WettbewerbStatus neuerStatus) {

				countChangeWettbewerbStatus++;
				return true;
			}

			@Override
			public void changeWettbewerb(final Wettbewerb wettbewerb) {

				countWettbewerbUpdate++;

				if (wettbewerb.id().jahr().equals(Integer.valueOf(2011)) || wettbewerb.id().jahr().equals(Integer.valueOf(2015))) {

					throw new PersistenceException("Wettbewerb mit diesem Jahr ist verboten");
				}
			}

			@Override
			public void addWettbewerb(final Wettbewerb wettbewerb) {

				countWettbewerbInsert++;

				if (wettbewerb.id().jahr().equals(Integer.valueOf(2011))) {

					throw new PersistenceException("Wettbewerb mit diesem Jahr ist verboten");
				}

				wettbewerbe.add(wettbewerb);

			}
		};
	}

	protected WettbewerbRepository getWettbewerbRepository() {

		return this.wettbewerbRepository;
	}

	protected int getCountWettbewerbInsert() {

		return countWettbewerbInsert;
	}

	protected int getCountWettbewerbUpdate() {

		return countWettbewerbUpdate;
	}

	protected int getCountChangeWettbewerbStatus() {

		return countChangeWettbewerbStatus;
	}

}
