// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.error.MkWettbewerbRuntimeException;
import de.egladil.web.mk_wettbewerb.domain.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.TemporaerePersistentePrivatteilnahme;

/**
 * PrivatteilnahmenMigrationService
 */
@ApplicationScoped
@Deprecated(forRemoval = true)
public class PrivatteilnahmenMigrationService {

	private static final Logger LOG = LoggerFactory.getLogger(PrivatteilnahmenMigrationService.class);

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	VeranstalterRepository veranstalterRepository;

	public static PrivatteilnahmenMigrationService createForTest(final TeilnahmenRepository teilnahmenRepository, final VeranstalterRepository veranstalterRepository) {

		PrivatteilnahmenMigrationService result = new PrivatteilnahmenMigrationService();
		result.teilnahmenRepository = teilnahmenRepository;
		result.veranstalterRepository = veranstalterRepository;
		return result;

	}

	public void run() {

		List<TemporaerePersistentePrivatteilnahme> temporaereTeilnahmen = teilnahmenRepository.loadAllTemporaryPrivatteilnahmen();
		List<Veranstalter> veranstalter = veranstalterRepository.loadPrivatveranstalter();
		List<Teilnahme> vorhandenePrivatteilnahmen = teilnahmenRepository.loadAllPrivatteilnahmen();

		LOG.info("Starte import von {} Privatteilnahme(n)", temporaereTeilnahmen.size());

		try {

			this.importieren(temporaereTeilnahmen, veranstalter, vorhandenePrivatteilnahmen);

		} catch (Exception e) {

			String msg = "Da ist beim Import der Privatteilnahmen leider etwas schiefgelaufen.";
			LOG.error(msg + " " + e.getMessage(), e);
			throw new MkWettbewerbRuntimeException(msg);

		}
	}

	@Transactional
	void importieren(final List<TemporaerePersistentePrivatteilnahme> temporaereTeilnahmen, final List<Veranstalter> veranstalter, final List<Teilnahme> vorhandenePrivatteilnahmen) {

		for (TemporaerePersistentePrivatteilnahme temporaereTeilnahme : temporaereTeilnahmen) {

			Optional<Veranstalter> optVeranstalter = veranstalter.stream()
				.filter(v -> v.person().uuid().equals(temporaereTeilnahme.getVeranstalterUuid())).findFirst();

			if (optVeranstalter.isPresent()) {

				Veranstalter derVeranstalter = optVeranstalter.get();

				if (derVeranstalter.teilnahmeIdentifier().size() != 1) {

					LOG.warn("Privatteilnahme {} kann nicht übernommen werden: es gibt kein eindeutiges teilnahmeID-Mapping",
						temporaereTeilnahme);

				} else {

					Identifier identifier = derVeranstalter.teilnahmeIdentifier().get(0);
					String teilnahmenummerNeu = identifier.identifier();
					Integer jahr = Integer.valueOf(temporaereTeilnahme.getWettbewerbUuid());

					Optional<Teilnahme> optVorhandene = vorhandenePrivatteilnahmen.stream()
						.filter(t -> t.teilnahmenummer().equals(identifier) && t.wettbewerbID().equals(new WettbewerbID(jahr)))
						.findFirst();

					if (optVorhandene.isEmpty()) {

						Privatteilnahme teilnahme = new Privatteilnahme(new WettbewerbID(jahr), identifier);
						teilnahmenRepository.addTeilnahme(teilnahme);
						temporaereTeilnahme.setTeilnahmenummerNeu(teilnahmenummerNeu);
						teilnahmenRepository.save(temporaereTeilnahme);

					} else {

						LOG.warn("Privatteilnahme {} kann nicht übernommen werden: es gibt sie bereits", temporaereTeilnahme);
					}
				}
			} else {

				LOG.warn("Privatteilnahme {} kann nicht übernommen werden: es gibt keinen Veranstalter mit der veranstalterUuid",
					temporaereTeilnahme);
			}
		}
	}
}
