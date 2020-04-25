// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.egladil.web.mk_wettbewerb.domain.model.personen.Lehrer;
import de.egladil.web.mk_wettbewerb.domain.model.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.model.personen.Privatperson;
import de.egladil.web.mk_wettbewerb.domain.model.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.model.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl.VeranstalterHibernateRepository;

/**
 * AbstractDomainServiceTest
 */
public abstract class AbstractDomainServiceTest {

	protected static final String UUID_LEHRER = "gudgfoiwo";

	protected static final String UUID_PRIVAT = "sagfigoso";

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
		Privatperson privatperson = new Privatperson(new Person(UUID_PRIVAT, "Herta Grütze"));

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
