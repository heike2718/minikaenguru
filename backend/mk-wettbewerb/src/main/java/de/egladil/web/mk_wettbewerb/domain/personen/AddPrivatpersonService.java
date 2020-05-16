// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;

/**
 * AddPrivatpersonService
 */
@RequestScoped
@DomainService
public class AddPrivatpersonService {

	@Inject
	VeranstalterRepository repository;

	public static AddPrivatpersonService createForTest(final VeranstalterRepository veranstalterRepository) {

		AddPrivatpersonService result = new AddPrivatpersonService();
		result.repository = veranstalterRepository;
		return result;
	}

	/**
	 * Persistiert eine neue Privatperson.
	 *
	 * @param data
	 *             CreateOrUpdatePrivatpersonCommand
	 */
	@Transactional
	public void addPrivatperson(final CreateOrUpdatePrivatpersonCommand data) {

		Optional<Veranstalter> optPrivatperson = repository.ofId(new Identifier(data.uuid()));

		if (optPrivatperson.isPresent()) {

			return;
		}

		Privatperson privatperson = new Privatperson(new Person(data.uuid(), data.fullName()),
			Arrays.asList(new Identifier(data.uuid())));

		repository.addVeranstalter(privatperson);
	}
}
