// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.statistik.AuswertungsmodusInfoService;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Auswertungsmodus;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;

/**
 * SchulenOverviewService
 */
@ApplicationScoped
@DomainService
public class SchulenOverviewService {

	private static final Logger LOG = LoggerFactory.getLogger(SchulenOverviewService.class);

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	LoggableEventDelegate eventDelegate;

	@Inject
	AuswertungsmodusInfoService auswertungsmodusInfoService;

	public static SchulenOverviewService createForTest(final VeranstalterRepository veranstalterRepo, final AktuelleTeilnahmeService teilnahmenService, final AuswertungsmodusInfoService auswertungsmodusInfoService) {

		SchulenOverviewService result = new SchulenOverviewService();
		result.aktuelleTeilnahmeService = teilnahmenService;
		result.veranstalterRepository = veranstalterRepo;
		result.auswertungsmodusInfoService = auswertungsmodusInfoService;
		return result;

	}

	public List<SchuleAPIModel> ermittleAnmeldedatenFuerSchulen(final Identifier lehrerID) {

		List<SchuleAPIModel> items = new ArrayList<>();

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(lehrerID);

		if (optVeranstalter.isEmpty()) {

			String msg = "Unzulässiger Zugriff auf Schulen durch UUID " + lehrerID
				+ ": es gibt keinen Veranstalter mit dieser UUID.";

			LOG.warn(msg);

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);

			return items;
		}

		final List<Identifier> schulIDs = optVeranstalter.get().teilnahmeIdentifier();

		if (schulIDs == null || schulIDs.isEmpty()) {

			return items;
		}

		for (Identifier schulID : schulIDs) {

			Optional<Teilnahme> optTeilnahme = aktuelleTeilnahmeService.aktuelleTeilnahme(schulID.identifier());

			if (optTeilnahme.isPresent()) {

				Auswertungsmodus auswertungsmodus = auswertungsmodusInfoService
					.ermittleAuswertungsmodusFuerTeilnahme(optTeilnahme.get().teilnahmeIdentifier());

				items.add(SchuleAPIModel.withKuerzel(schulID.identifier()).withAngemeldet(true)
					.withAuswertungsmodus(auswertungsmodus));

			} else {

				items.add(SchuleAPIModel.withKuerzel(schulID.identifier())
					.withAuswertungsmodus(Auswertungsmodus.INDIFFERENT));
			}
		}

		return items;
	}
}
