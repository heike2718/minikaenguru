// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.admin;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
import de.egladil.web.mk_gateway.domain.statistik.AnonymisierteTeilnahmenService;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchuleDetailsService;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleDetails;

/**
 * AdminSchulenService
 */
@ApplicationScoped
public class AdminSchulenService {

	private static final Logger LOG = LoggerFactory.getLogger(AdminSchulenService.class);

	@Inject
	SchuleDetailsService schuleDetailsService;

	@Inject
	SchulkatalogService schulkatalogService;

	@Inject
	AnonymisierteTeilnahmenService anonymisierteTeilnahmenService;

	/**
	 * Sammelt die Schuldaten mit den Details zusammen.
	 *
	 * @param  schulkuerzel
	 * @return
	 */
	public Optional<SchuleAdminOverview> ermittleSchuleMitDetails(final String schulkuerzel, final String adminUuid) {

		SchuleDetails schuleDetails = schuleDetailsService.ermittleSchuldetails(new Identifier(schulkuerzel), null);

		if (schuleDetails == null) {

			LOG.warn("Keine Schuldetails mit kuerzel {} gefunden", schulkuerzel);
			return Optional.empty();

		}

		Optional<SchuleAPIModel> optSchuleAPIModel = schulkatalogService.findSchuleQuietly(schulkuerzel);

		SchuleAPIModel data = null;

		if (optSchuleAPIModel.isPresent()) {

			data = optSchuleAPIModel.get().withDetails(schuleDetails)
				.withAngemeldet(schuleDetails.aktuelleTeilnahme() != null);
		} else {

			data = SchuleAPIModel.withKuerzel(schuleDetails.kuerzel()).withAngemeldet(schuleDetails.aktuelleTeilnahme() != null)
				.withDetails(schuleDetails);
		}

		List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = anonymisierteTeilnahmenService
			.loadAnonymisierteTeilnahmen(schulkuerzel, adminUuid);

		SchuleKatalogData schuleKatalogData = SchuleKatalogData.fromSchuleAPIModel(data);
		SchuleMinikaenguruData schuleMinikaenguruData = SchuleMinikaenguruData.createFromSchuleDetails(schuleDetails);

		SchuleAdminOverview result = new SchuleAdminOverview().withKatalogData(schuleKatalogData)
			.withMinikaenguruData(schuleMinikaenguruData).withSchulteilnahmen(anonymisierteTeilnahmen);

		if (schuleMinikaenguruData.aktuellAngemeldet()) {

			result = result.withAktuelleTeilnahme(
				AktuelleSchulteilnahmeData.createFromSchulteilnahmeAPIModel(schuleDetails.aktuelleTeilnahme()));
		}
		return Optional.of(result);
	}

}
