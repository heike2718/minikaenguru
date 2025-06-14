// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogEntitiesMapper;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.statistik.AuswertungsmodusInfoService;
import de.egladil.web.mk_gateway.domain.teilnahmen.AktuelleTeilnahmeService;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchuleDetailsService;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchulenOverviewService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Auswertungsmodus;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleDetails;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.dao.KatalogeRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Schule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * SchulenAnmeldeinfoService
 */
@ApplicationScoped
@DomainService
public class SchulenAnmeldeinfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchulenAnmeldeinfoService.class);

	@Inject
	AuthorizationService authorizationService;

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	LoggableEventDelegate eventDelegate;

	@Inject
	SchulenOverviewService schulenOverviewService;

	@Inject
	SchuleDetailsService schuleDetailsService;

	@Inject
	KatalogeRepository katalogeRepository;

	@Inject
	AuswertungsmodusInfoService auswertungsmodusInfoService;

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	public List<SchuleAPIModel> findSchulenMitAnmeldeinfo(final String lehrerUUID) {

		List<SchuleAPIModel> schulenOfLehrer = this.schulenOverviewService
			.ermittleAnmeldedatenFuerSchulen(new Identifier(lehrerUUID));

		if (schulenOfLehrer == null || schulenOfLehrer.isEmpty()) {

			return new ArrayList<>();
		}

		List<String> kuerzel = schulenOfLehrer.stream().map(s -> s.kuerzel()).collect(Collectors.toList());
		List<Schule> trefferliste = katalogeRepository.findSchulenWithKuerzeln(kuerzel);

		final SchulkatalogEntitiesMapper mapper = new SchulkatalogEntitiesMapper();
		final List<SchuleAPIModel> schulenAusKatalg = trefferliste.stream().map(s -> mapper.mapSchuleToSchuleAPIModel(s)).toList();

		return mergeDataFromSchulenOfLehrer(schulenAusKatalg, schulenOfLehrer);
	}

	/**
	 * Läd die Details für die Schule des gegebenen Lehrers aus den Katalogen und aus der Wettbewerbe-API.
	 *
	 * @param schulkuerzel
	 * @param lehrerId String UUID eines Lehrers.
	 * @return SchuleAPIModel
	 */
	public SchuleAPIModel getSchuleWithWettbewerbsdetails(final String schulkuerzel, final String lehrerId) {

		String kontext = "[getSchuleDetails - " + schulkuerzel + "]";
		authorizationService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(lehrerId), new Identifier(schulkuerzel),
			kontext);

		Optional<Schule> optSchule = katalogeRepository.findSchuleWithKuerzel(schulkuerzel);

		SchuleAPIModel schuleAusKatalog = null;

		if (optSchule.isEmpty()) {
			LOGGER.error("Kein Katalogeintrag für Schule - kuerzel={}, Lehrer-UUID={}",
				schulkuerzel, StringUtils.abbreviate(lehrerId, 11));
			schuleAusKatalog = new SchuleAPIModel().withKuerzel(schulkuerzel).markKatalogeintragUnknown();
		} else {
			schuleAusKatalog = new SchulkatalogEntitiesMapper().mapSchuleToSchuleAPIModel(optSchule.get());
		}

		SchuleDetails schuleDetails = schuleDetailsService.ermittleSchuldetails(new Identifier(schulkuerzel),
			new Identifier(lehrerId));

		SchuleAPIModel result = SchuleAPIModel.merge(schuleAusKatalog, schuleDetails);

		Optional<Teilnahme> optAktuelleTeilnahme = aktuelleTeilnahmeService.aktuelleTeilnahme(schulkuerzel);

		if (optAktuelleTeilnahme.isPresent()) {

			Teilnahme teilnahme = optAktuelleTeilnahme.get();
			Auswertungsmodus auswertungsmodus = auswertungsmodusInfoService
				.ermittleAuswertungsmodusFuerTeilnahme(teilnahme.teilnahmeIdentifier());

			return result.withAktuellAngemeldet(true).withAuswertungsmodus(auswertungsmodus);
		}

		return result.withAuswertungsmodus(Auswertungsmodus.INDIFFERENT);

	}

	/**
	 * Führt die Schuldaten aus verschiedenen Quellen zusammen.
	 *
	 * @param schulenAusKatalg
	 * @param schulenOfLehrer
	 * @return
	 */
	List<SchuleAPIModel> mergeDataFromSchulenOfLehrer(final List<SchuleAPIModel> schulenAusKatalg,
		final List<SchuleAPIModel> schulenOfLehrer) {

		final List<SchuleAPIModel> nurLehrer = schulenAusKatalg.stream().filter(s -> schulenOfLehrer.contains(s))
			.collect(Collectors.toList());

		nurLehrer.stream().forEach(schule -> {

			Optional<SchuleAPIModel> opt = schulenOfLehrer.stream().filter(ks -> ks.kuerzel().equals(schule.kuerzel())).findFirst();

			if (opt.isPresent()) {

				SchuleAPIModel schuleAPIModel = opt.get();
				schule.withAktuellAngemeldet(schuleAPIModel.aktuellAngemeldet())
					.withAuswertungsmodus(schuleAPIModel.getAuswertungsmodus());
			}
		});

		if (nurLehrer.size() != schulenOfLehrer.size()) {

			String msg = "Nicht alle Schulen auf beiden Seiten gefunden: Kataloge: " + schulenAusKatalg.toString() + ", Lehrer: "
				+ schulenOfLehrer.toString();

			LOGGER.warn(msg);

			eventDelegate.fireDataInconsistencyEvent(msg, domainEventHandler);
		}

		if (schulenOfLehrer.size() > schulenAusKatalg.size()) {

			List<SchuleAPIModel> fehlendeLehrer = schulenOfLehrer.stream().filter(s -> !schulenAusKatalg.contains(s))
				.collect(Collectors.toList());

			fehlendeLehrer.forEach(s -> {

				s.markKatalogeintragUnknown();
				nurLehrer.add(s);
			});
		}

		return nurLehrer;
	}
}
