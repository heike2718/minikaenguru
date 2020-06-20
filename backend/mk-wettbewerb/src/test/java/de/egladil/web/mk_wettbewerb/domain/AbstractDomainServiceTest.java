// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import org.mockito.Mockito;

import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_wettbewerb.testdaten.InMemoryTeilnahmenRepository;
import de.egladil.web.mk_wettbewerb.testdaten.InMemoryVeranstalterRepository;

/**
 * AbstractDomainServiceTest
 */
public abstract class AbstractDomainServiceTest {

	protected static final String UUID_LEHRER_1 = "UUID_LEHRER_1";

	protected static final String UUID_LEHRER_2 = "UUID_LEHRER_2";

	protected static final String UUID_LEHRER_3 = "UUID_LEHRER_3";

	protected static final String UUID_PRIVAT = "UUID_PRIVAT";

	protected static final String UUID_PRIVAT_GESPERRT = "UUID_PRIVAT_GESPERRT";

	protected static final String UUID_PRIVAT_NICHT_ANGEMELDET = "UUID_PRIVAT_NICHT_ANGEMELDET";

	protected static final String SCHULKUERZEL_1 = "SCHULKUERZEL_1";

	protected static final String SCHULKUERZEL_2 = "SCHULKUERZEL_2";

	protected static final String SCHULKUERZEL_3 = "SCHULKUERZEL_3";

	protected static final String TEILNAHMENUMMER_PRIVAT = "TEILNAHMENUMMER_PRIVAT";

	protected static final String TEILNAHMENUMMER_PRIVAT_GESPERRT = "TEILNAHMENUMMER_PRIVAT_GESPERRT";

	protected static final String TEILNAHMENUMMER_PRIVAT_NICHT_ANGEMELDET = "TEILNAHMENUMMER_PRIVAT_NICHT_ANGEMELDET";

	protected static final Integer WETTBEWERBSJAHR_AKTUELL = 2020;

	private InMemoryVeranstalterRepository veranstalterRepository;

	private InMemoryTeilnahmenRepository teilnahmenRepository;

	private WettbewerbRepository wettbewerbRepository;

	private Wettbewerb aktuellerWettbewerb;

	protected void setUp() {

		aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL)).withStatus(WettbewerbStatus.ANMELDUNG)
			.withWettbewerbsbeginn(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JANUARY, 1))
			.withDatumFreischaltungLehrer(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.AUGUST, 1));

		wettbewerbRepository = Mockito.mock(WettbewerbRepository.class);

		Mockito.when(wettbewerbRepository.loadWettbewerbWithMaxJahr()).thenReturn(Optional.of(aktuellerWettbewerb));

		veranstalterRepository = new InMemoryVeranstalterRepository();

		teilnahmenRepository = new InMemoryTeilnahmenRepository();

	}

	protected InMemoryVeranstalterRepository getVeranstalterRepository() {

		return veranstalterRepository;
	}

	protected WettbewerbRepository getMockitoBasedWettbewerbRepository() {

		return wettbewerbRepository;
	}

	protected InMemoryTeilnahmenRepository getTeilnahmenRepository() {

		return teilnahmenRepository;
	}

	protected Wettbewerb getAktuellerWettbewerb(final Integer aktuellesJahr) {

		if (WETTBEWERBSJAHR_AKTUELL.equals(aktuellesJahr)) {

			return wettbewerbRepository.loadWettbewerbWithMaxJahr().get();
		}

		return null;
	}

	protected Wettbewerb createWettbewerb(final Integer jahr, final WettbewerbStatus status) {

		return new Wettbewerb(new WettbewerbID(jahr)).withStatus(status)
			.withWettbewerbsbeginn(LocalDate.of(jahr, Month.JANUARY, 1))
			.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1));

	}

}
