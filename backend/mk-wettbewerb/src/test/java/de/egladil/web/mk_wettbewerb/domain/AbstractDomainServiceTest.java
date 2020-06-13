// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.egladil.web.mk_wettbewerb.domain.personen.Lehrer;
import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.personen.Privatperson;
import de.egladil.web.mk_wettbewerb.domain.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl.VeranstalterHibernateRepository;

/**
 * AbstractDomainServiceTest
 */
public abstract class AbstractDomainServiceTest {

	protected static final String UUID_LEHRER_1 = "gudgfoiwo";

	protected static final String UUID_LEHRER_2 = "GAUGGHWQUO";

	protected static final String UUID_PRIVAT = "8efc761c-83e7-42e3-bd2c-ba29b0db80dc";

	protected static final String SCHULKUERZEL_1 = "AGDFWGJOPJ";

	protected static final String SCHULKUERZEL_2 = "UZTGF65FR3";

	protected static final Integer WETTBEWERBSJAHR_AKTUELL = 2020;

	private Map<Identifier, Veranstalter> veranstalterMap = new HashMap<>();

	private VeranstalterRepository veranstalterRepository;

	private List<Teilnahme> teilnahmen = new ArrayList<>();

	private TeilnahmenRepository teilnahmenRepository;

	private WettbewerbRepository wettbewerbRepository;

	private Wettbewerb aktuellerWettbewerb;

	private int countPrivatpersonAdded = 0;

	private int countLehrerAdded = 0;

	private int countPrivatpersonChanged = 0;

	private int countLehrerChanged = 0;

	protected void setUp() {

		aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL)).withStatus(WettbewerbStatus.BEENDET)
			.withWettbewerbsbeginn(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JANUARY, 1))
			.withDatumFreischaltungLehrer(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(WETTBEWERBSJAHR_AKTUELL, Month.AUGUST, 1));

		List<Identifier> teilnahmenLehrer1 = new ArrayList<>();
		teilnahmenLehrer1.add(new Identifier(SCHULKUERZEL_1));
		teilnahmenLehrer1.add(new Identifier(SCHULKUERZEL_2));

		Lehrer lehrer1 = new Lehrer(new Person(UUID_LEHRER_1, "Hans Wurst"), teilnahmenLehrer1);

		List<Identifier> teilnahmenLehrer2 = new ArrayList<>();
		teilnahmenLehrer2.add(new Identifier(SCHULKUERZEL_1));

		Lehrer lehrer2 = new Lehrer(new Person(UUID_LEHRER_2, "Olle Keule"), teilnahmenLehrer2);

		Privatperson privatperson = new Privatperson(new Person(UUID_PRIVAT, "Herta Grütze"),
			Arrays.asList(new Identifier(UUID_PRIVAT)));

		veranstalterRepository = createVeranstalterRepo();

		veranstalterRepository.addVeranstalter(privatperson);
		veranstalterRepository.addVeranstalter(lehrer1);
		veranstalterRepository.addVeranstalter(lehrer2);

		teilnahmenRepository = createTeilnahmenRepo();
		wettbewerbRepository = createWettbewerbRepo();

		Schulteilnahme schuleilnahme = new Schulteilnahme(aktuellerWettbewerb.id(), new Identifier(SCHULKUERZEL_1), "Christaschule",
			new Identifier(UUID_LEHRER_1));
		teilnahmenRepository.addTeilnahme(schuleilnahme);

		Privatteilnahme privatteilnahme = new Privatteilnahme(aktuellerWettbewerb.id(), new Identifier(UUID_PRIVAT));
		teilnahmenRepository.addTeilnahme(privatteilnahme);

	}

	private WettbewerbRepository createWettbewerbRepo() {

		return new WettbewerbRepository() {

			@Override
			public Optional<Wettbewerb> loadWettbewerbWithMaxJahr() {

				return Optional.of(aktuellerWettbewerb);
			}
		};
	}

	/**
	 * @return
	 */
	private TeilnahmenRepository createTeilnahmenRepo() {

		return new TeilnahmenRepository() {

			@Override
			public Optional<Teilnahme> ofTeilnahmenummerArtWettbewerb(final String teilnahmenummer, final Teilnahmeart art, final WettbewerbID wettbewerbId) {

				return teilnahmen.stream().filter(t -> teilnahmenummer.equals(t.teilnahmenummer().identifier())
					&& art.equals(t.teilnahmeart()) && wettbewerbId.equals(t.wettbewerbID())).findFirst();
			}

			@Override
			public List<Teilnahme> ofTeilnahmenummerArt(final String teilnahmenummer, final Teilnahmeart art) {

				return teilnahmen.stream().filter(t -> teilnahmenummer.equals(t.teilnahmenummer().identifier())
					&& art.equals(t.teilnahmeart())).collect(Collectors.toList());
			}

			@Override
			public List<Teilnahme> ofTeilnahmenummer(final String teilnahmenummer) {

				return teilnahmen.stream().filter(t -> teilnahmenummer.equals(t.teilnahmenummer().identifier()))
					.collect(Collectors.toList());
			}

			@Override
			public void changeTeilnahme(final Schulteilnahme teilnahme, final String uuidAenderer) throws IllegalStateException {

				Optional<Teilnahme> opt = teilnahmen.stream().filter(t -> t.equals(teilnahme)).findFirst();

				if (opt.isPresent()) {

					teilnahmen.remove(opt.get());
					teilnahmen.add(teilnahme);
				}

			}

			@Override
			public void addTeilnahme(final Teilnahme teilnahme) {

				if (!teilnahmen.contains(teilnahme)) {

					teilnahmen.add(teilnahme);
				}
			}
		};
	}

	/**
	 * @return
	 */
	private VeranstalterHibernateRepository createVeranstalterRepo() {

		return new VeranstalterHibernateRepository() {
			@Override
			public Optional<Veranstalter> ofId(final Identifier identifier) {

				Veranstalter veranstalter = veranstalterMap.get(identifier);
				return veranstalter == null ? Optional.empty() : Optional.of(veranstalter);
			}

			@Override
			public void changeVeranstalter(final Veranstalter veranstalter) throws IllegalStateException {

				switch (veranstalter.rolle()) {

				case PRIVAT:
					countPrivatpersonChanged++;
					break;

				case LEHRER:
					countLehrerChanged++;
					break;

				default:
					break;
				}

			}

			@Override
			public void addVeranstalter(final Veranstalter veranstalter) {

				veranstalterMap.put(new Identifier(veranstalter.uuid()), veranstalter);

				switch (veranstalter.rolle()) {

				case PRIVAT:
					countPrivatpersonAdded++;
					break;

				case LEHRER:
					countLehrerAdded++;
					break;

				default:
					break;
				}

			}
		};
	}

	protected VeranstalterRepository getVeranstalterRepository() {

		return veranstalterRepository;
	}

	protected int getCountPrivatpersonAdded() {

		return countPrivatpersonAdded;
	}

	protected int getCountLehrerAdded() {

		return countLehrerAdded;
	}

	protected int getCountPrivatpersonChanged() {

		return countPrivatpersonChanged;
	}

	protected int getCountLehrerChanged() {

		return countLehrerChanged;
	}

	protected WettbewerbRepository getWettbewerbRepository() {

		return wettbewerbRepository;
	}

	protected TeilnahmenRepository getTeilnahmenRepository() {

		return teilnahmenRepository;
	}

}
