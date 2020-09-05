// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.ChangeUserCommand;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * SynchronizeVeranstalterService
 */
@ApplicationScoped
public class SynchronizeVeranstalterService {

	private static final Logger LOG = LoggerFactory.getLogger(SynchronizeVeranstalterService.class);

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	SchulkollegienService schulkollegienService;

	@Transactional
	public void changeVeranstalterDaten(final ChangeUserCommand command) {

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(new Identifier(command.uuid()));

		if (optVeranstalter.isEmpty()) {

			LOG.info("Veranstalter mit UUID {} unbekannt. Wird ignoriert", command.uuid());

			return;
		}

		Veranstalter veranstalter = optVeranstalter.get();

		Veranstalter merged = veranstalter.merge(new Person(command.uuid(), command.fullName()).withEmail(command.email()));

		veranstalterRepository.changeVeranstalter(merged);

		LOG.info("Daten aus authprovider nach VERANSTALTER uebernommen: {}", command);

		if (Rolle.LEHRER == veranstalter.rolle()) {

			Lehrer lehrer = (Lehrer) merged;

			String schulkuerzel = lehrer.joinedSchulen();

			LehrerChanged lehrerChanged = new LehrerChanged(merged.person(), schulkuerzel, schulkuerzel,
				lehrer.isNewsletterEmpfaenger());

			this.schulkollegienService.handleLehrerChanged(lehrerChanged);

			LOG.info("Daten aus authprovider nach SCHULKOLLEGIEN uebernommen: schulkuerzel={}", schulkuerzel);
		}
	}
}
