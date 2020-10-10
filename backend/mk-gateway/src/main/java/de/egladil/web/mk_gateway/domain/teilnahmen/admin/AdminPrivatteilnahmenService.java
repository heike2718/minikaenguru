// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.admin;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.statistik.AnonymisierteTeilnahmenService;
import de.egladil.web.mk_gateway.domain.teilnahmen.AktuelleTeilnahmeService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.PrivatteilnahmeAPIModel;

/**
 * AdminPrivatteilnahmenService
 */
@ApplicationScoped
@DomainService
public class AdminPrivatteilnahmenService {

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	@Inject
	AnonymisierteTeilnahmenService anonymisierteTeilnahmenService;

	/**
	 * Läd die Details zur gegebenen teilnahmenummer.
	 *
	 * @param  teilnahmenummer
	 * @return                 Optional
	 */
	public Optional<PrivatteilnahmeAdminOverview> ermittlePrivatteilnahmeMitDetails(final String teilnahmenummer, final String userUuid) {

		List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = anonymisierteTeilnahmenService
			.loadAnonymisierteTeilnahmen(teilnahmenummer, userUuid);

		if (anonymisierteTeilnahmen.isEmpty()) {

			return Optional.empty();
		}

		PrivatteilnahmeAdminOverview result = new PrivatteilnahmeAdminOverview().withTeilnahmen(anonymisierteTeilnahmen);

		Optional<Teilnahme> optAktuelle = aktuelleTeilnahmeService.aktuelleTeilnahme(teilnahmenummer);

		if (optAktuelle.isPresent()) {

			Teilnahme aktuelle = optAktuelle.get();
			PrivatteilnahmeAPIModel apiModel = PrivatteilnahmeAPIModel.createFromPrivatteilnahme((Privatteilnahme) aktuelle);

			result = result.withAktuelleTeilnahme(apiModel);
		}

		return Optional.of(result);

	}

}
