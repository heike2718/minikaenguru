// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;

/**
 * LehrerService
 */
@RequestScoped
@DomainService
public class LehrerService {

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	Event<LehrerRegisteredForSchule> lehrerRegisteredForSchule;

	private LehrerRegisteredForSchule event;

	public static LehrerService createServiceForTest(final VeranstalterRepository lehrerRepository) {

		LehrerService result = new LehrerService();
		result.veranstalterRepository = lehrerRepository;
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

		event = new LehrerRegisteredForSchule(person, data.schulkuerzel());

		if (lehrerRegisteredForSchule != null) {

			lehrerRegisteredForSchule.fire(event);
		}

	}

	LehrerRegisteredForSchule event() {

		return event;
	}

}
