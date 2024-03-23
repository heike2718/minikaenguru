// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.veranstalter.PrivatteilnahmeKuerzelService;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagenService;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.InMemoryKinderRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.InMemoryKlassenRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.InMemoryLoesungszettelRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.InMemoryNewsletterRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.InMemorySchulkollegienRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.InMemoryTeilnahmenRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.InMemoryUserRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.InMemoryVeranstalterRepository;
import jakarta.persistence.PersistenceException;

/**
 * AbstractDomainServiceTest
 */
public abstract class AbstractDomainServiceTest {

	public static final String VERSANDINFO_TEST_UUID = "VERSANDINFO_TEST_UUID";

	public static final String VERSANDINFO_ALLE_UUID = "VERSANDINFO_ALLE_UUID";

	public static final String VERSANDINFO_LEHRER_UUID = "VERSANDINFO_LEHRER_UUID";

	public static final String VERSANDINFO_LEHRER__BEREITS_VERSENDET_UUID = "VERSANDINFO_LEHRER__BEREITS_VERSENDET_UUID";

	public static final String VERSANDINFO_PRIVATVERANSTALTER_UUID = "VERSANDINFO_PRIVATVERANSTALTER_UUID";

	public static final String NEWSLETTER_TEST_UND_ALLE_UUID = "NEWSLETTER_TEST_UND_ALLE_UUID";

	public static final String NEWSLETTER_LEHRER_UUID = "NEWSLETTER_LEHRER_UUID";

	public static final String NEWSLETTER_PRIVATVERANSTALTER_UUID = "NEWSLETTER_PRIVATVERANSTALTER_UUID";

	protected static final String UUID_LEHRER_1 = "UUID_LEHRER_1";

	protected static final String UUID_LEHRER_2 = "UUID_LEHRER_2";

	protected static final String UUID_LEHRER_3 = "UUID_LEHRER_3";

	protected static final String UUID_LEHRER_GESPERRT = "UUID_LEHRER_GESPERRT";

	protected static final String UUID_LEHRER_ANDERE_SCHULE = "UUID_LEHRER_ANDERE_SCHULE";

	protected static final String UUID_LEHRER_OHNE_SCHULE = "UUID_LEHRER_OHNE_SCHULE";

	protected static final String UUID_PRIVAT = "UUID_PRIVAT";

	protected static final String UUID_PRIVAT_GESPERRT = "UUID_PRIVAT_GESPERRT";

	protected static final String UUID_PRIVAT_NICHT_ANGEMELDET = "UUID_PRIVAT_NICHT_ANGEMELDET";

	protected static final String UUID_PRIVAT_MEHRERE_TEILNAHMEKURZEL = "UUID_PRIVAT_MEHRERE_TEILNAHMEKURZEL";

	protected static final String UUID_PRIVAT_KEIN_TEILNAHMEKURZEL = "UUID_PRIVAT_KEIN_TEILNAHMEKURZEL";

	protected static final String SCHULKUERZEL_1 = "SCHULKUERZEL_1";

	protected static final String SCHULKUERZEL_2 = "SCHULKUERZEL_2";

	protected static final String SCHULKUERZEL_3 = "SCHULKUERZEL_3";

	protected static final String TEILNAHMENUMMER_PRIVAT = "TEILNAHMENUMMER_PRIVAT";

	protected static final String TEILNAHMENUMMER_PRIVAT_GESPERRT = "TEILNAHMENUMMER_PRIVAT_GESPERRT";

	protected static final String TEILNAHMENUMMER_PRIVAT_NICHT_ANGEMELDET = "TEILNAHMENUMMER_PRIVAT_NICHT_ANGEMELDET";

	protected static final Integer WETTBEWERBSJAHR_AKTUELL = 2020;

	protected static final Integer WETTBEWERBSJAHR_ERFASST = 2017;

	private InMemoryUserRepository userRepository;

	private InMemoryVeranstalterRepository veranstalterRepository;

	private InMemoryTeilnahmenRepository teilnahmenRepository;

	private InMemoryKlassenRepository klassenRepository;

	private Wettbewerb aktuellerWettbewerb;

	private Wettbewerb erfassterWettbewerb;

	private ZugangUnterlagenService zugangUnterlagenService;

	private WettbewerbService wettbewerbService;

	private PrivatteilnahmeKuerzelService privatteilnameKuerzelService;

	private WettbewerbRepository wettbewerbRepository;

	private InMemoryLoesungszettelRepository loesungszettelRepository;

	private InMemoryKinderRepository kinderRepository;

	private InMemorySchulkollegienRepository schulkollegienRepository;

	private InMemoryNewsletterRepository newsletterRepository;

	private int countWettbewerbInsert = 0;

	private int countWettbewerbUpdate = 0;

	private int countChangeWettbewerbStatus;

	protected void setUp() {

		aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL)).withStatus(WettbewerbStatus.ANMELDUNG)
			.withWettbewerbsbeginn(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JANUARY, 1))
			.withDatumFreischaltungLehrer(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.AUGUST, 1));

		erfassterWettbewerb = new Wettbewerb(new WettbewerbID(WETTBEWERBSJAHR_ERFASST)).withStatus(WettbewerbStatus.ERFASST)
			.withWettbewerbsbeginn(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JANUARY, 1))
			.withDatumFreischaltungLehrer(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.AUGUST, 1));

		wettbewerbRepository = Mockito.mock(WettbewerbRepository.class);

		Mockito.when(wettbewerbRepository.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] { aktuellerWettbewerb, erfassterWettbewerb }));

		userRepository = new InMemoryUserRepository();

		veranstalterRepository = new InMemoryVeranstalterRepository();

		teilnahmenRepository = new InMemoryTeilnahmenRepository();

		klassenRepository = new InMemoryKlassenRepository();

		loesungszettelRepository = new InMemoryLoesungszettelRepository();

		kinderRepository = new InMemoryKinderRepository();

		schulkollegienRepository = new InMemorySchulkollegienRepository();

		wettbewerbService = WettbewerbService.createForTest(wettbewerbRepository);

		zugangUnterlagenService = ZugangUnterlagenService.createForTest(teilnahmenRepository, veranstalterRepository,
			wettbewerbService);

		privatteilnameKuerzelService = new PrivatteilnahmeKuerzelService() {

			@Override
			public String neuesKuerzel() {

				return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
			}

		};

		List<Wettbewerb> wettbewerbe = new ArrayList<>();
		List<Integer> jahre = Arrays.asList(new Integer[] { 2005, 2020, 2010, 2015 });

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

			if (jahr.equals(2020)) {

				wettbewerbe.add(new Wettbewerb(new WettbewerbID(jahr)).withStatus(WettbewerbStatus.ANMELDUNG)
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

		newsletterRepository = new InMemoryNewsletterRepository();

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

			return createWettbewerb(WETTBEWERBSJAHR_AKTUELL, WettbewerbStatus.ANMELDUNG);
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

	protected WettbewerbRepository getWettbewerbRepository() {

		return wettbewerbRepository;
	}

	protected ZugangUnterlagenService getZugangUnterlagenService() {

		return zugangUnterlagenService;
	}

	protected WettbewerbService getWettbewerbService() {

		return wettbewerbService;
	}

	protected PrivatteilnahmeKuerzelService getPrivatteilnameKuerzelService() {

		return privatteilnameKuerzelService;
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

	protected InMemoryKlassenRepository getKlassenRepository() {

		return klassenRepository;
	}

	protected InMemoryLoesungszettelRepository getLoesungszettelRepository() {

		return loesungszettelRepository;
	}

	protected InMemoryKinderRepository getKinderRepository() {

		return kinderRepository;
	}

	protected InMemoryUserRepository getUserRepository() {

		return userRepository;
	}

	protected InMemorySchulkollegienRepository getSchulkollegienRepository() {

		return schulkollegienRepository;
	}

	protected InMemoryNewsletterRepository getNewsletterRepository() {

		return newsletterRepository;
	}
}
