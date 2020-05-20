// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_wettbewerb.domain.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;

/**
 * SchulenOverviewService
 */
@ApplicationScoped
@DomainService
public class SchulenOverviewService {

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	public List<SchuleAPIModel> ermittleAnmeldedatenFuerSchulen(final Identifier lehrerID) {

		List<SchuleAPIModel> items = new ArrayList<>();

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(lehrerID);

		if (optVeranstalter.isEmpty()) {

			return items;
		}

		final List<Identifier> schulIDs = optVeranstalter.get().teilnahmeIdentifier();

		for (Identifier schulID : schulIDs) {

			Optional<Teilnahme> optTeilnahme = aktuelleTeilnahmeService.aktuelleTeilnahme(schulID.identifier());

			if (optTeilnahme.isPresent()) {

				items.add(new SchuleAPIModel(schulID.identifier()).angemeldet());

			} else {

				items.add(new SchuleAPIModel(schulID.identifier()));
			}
		}

		return items;
	}
}
