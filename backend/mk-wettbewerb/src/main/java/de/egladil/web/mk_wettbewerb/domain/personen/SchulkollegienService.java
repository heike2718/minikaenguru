// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;

/**
 * SchulkollegienService
 */
@RequestScoped
@DomainService
public class SchulkollegienService {

	@Inject
	SchulkollegienRepository repository;

	static SchulkollegienService createForTest(final SchulkollegienRepository repository) {

		SchulkollegienService result = new SchulkollegienService();
		result.repository = repository;
		return result;
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	void handleSchuleLehrerAdded(@Observes final LehrerRegisteredForSchule event) {

		Optional<Schulkollegium> opt = repository.ofSchulkuerzel(new Identifier(event.schulkuerzel()));

		Identifier schulkuerzel = new Identifier(event.schulkuerzel());
		List<Person> alleLehrer = new ArrayList<>();

		if (opt.isPresent()) {

			Schulkollegium schulkollegium = opt.get();
			alleLehrer.addAll(schulkollegium.alleLehrerUnmodifiable());
			alleLehrer.add(event.person());
			this.repository.replaceKollegen(new Schulkollegium(schulkuerzel, alleLehrer.toArray(new Person[0])));
		} else {

			alleLehrer.add(event.person());
			Schulkollegium neuesSchulkollegium = new Schulkollegium(schulkuerzel, alleLehrer.toArray(new Person[0]));
			this.repository.addKollegium(neuesSchulkollegium);
		}
	}
}
