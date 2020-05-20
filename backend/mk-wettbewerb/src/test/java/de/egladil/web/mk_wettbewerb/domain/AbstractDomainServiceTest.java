// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.egladil.web.mk_wettbewerb.domain.personen.Lehrer;
import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.personen.Privatperson;
import de.egladil.web.mk_wettbewerb.domain.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl.VeranstalterHibernateRepository;

/**
 * AbstractDomainServiceTest
 */
public abstract class AbstractDomainServiceTest {

	protected static final String UUID_LEHRER = "gudgfoiwo";

	protected static final String UUID_PRIVAT = "8efc761c-83e7-42e3-bd2c-ba29b0db80dc";

	protected static final String SCHULKUERZEL = "AGDFWGJOPJ";

	private Map<Identifier, Veranstalter> veranstalterMap = new HashMap<>();

	private VeranstalterRepository repository;

	private int countPrivatpersonAdded = 0;

	private int countLehrerAdded = 0;

	private int countPrivatpersonChanged = 0;

	private int countLehrerChanged = 0;

	protected void setUp() {

		List<Identifier> teilnahmen = new ArrayList<>();
		teilnahmen.add(new Identifier(SCHULKUERZEL));
		Lehrer lehrer = new Lehrer(new Person(UUID_LEHRER, "Hans Wurst"), teilnahmen);
		Privatperson privatperson = new Privatperson(new Person(UUID_PRIVAT, "Herta Grütze"),
			Arrays.asList(new Identifier(UUID_PRIVAT)));

		repository = new VeranstalterHibernateRepository() {
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

		repository.addVeranstalter(privatperson);
		repository.addVeranstalter(lehrer);

	}

	protected VeranstalterRepository getVeranstalterRepository() {

		return repository;
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

}
