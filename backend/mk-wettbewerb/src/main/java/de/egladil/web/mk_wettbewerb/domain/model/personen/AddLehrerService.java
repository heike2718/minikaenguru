// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.personen;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import de.egladil.web.mk_wettbewerb.application.commands.CreateOrUpdateLehrerCommand;
import de.egladil.web.mk_wettbewerb.domain.model.Identifier;
import de.egladil.web.mk_wettbewerb.domain.model.events.SchuleLehrerAdded;

/**
 * AddLehrerService
 */
@RequestScoped
public class AddLehrerService {

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	Event<SchuleLehrerAdded> schuleLehrerAdded;

	private boolean test;

	private SchuleLehrerAdded event;

	public static AddLehrerService createServiceForTest(final VeranstalterRepository lehrerRepository) {

		AddLehrerService result = new AddLehrerService();
		result.veranstalterRepository = lehrerRepository;
		result.test = true;
		return result;

	}

	@Transactional
	public void addLehrer(final CreateOrUpdateLehrerCommand data) {

		Optional<Veranstalter> optLehrer = veranstalterRepository.ofId(new Identifier(data.uuid()));

		if (optLehrer.isPresent()) {

			return;
		}

		List<Identifier> schulkuerzel = Arrays.asList(new Identifier[] { new Identifier(data.schulkuerzel()) });
		Person person = new Person(data.uuid(), data.fullName());

		Lehrer lehrer = new Lehrer(person, schulkuerzel);

		veranstalterRepository.addVeranstalter(lehrer);

		event = new SchuleLehrerAdded(data.schulkuerzel(), person);

		if (!test) {

			schuleLehrerAdded.fire(event);
		}

	}

	SchuleLehrerAdded event() {

		return event;
	}

}
