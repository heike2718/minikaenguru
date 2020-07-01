// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_wettbewerb.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_wettbewerb.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_wettbewerb.domain.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;

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
	Event<SecurityIncidentRegistered> securityIncidentEvent;

	private SecurityIncidentRegistered securityIncidentRegistered;

	public static SchulenOverviewService createForTest(final VeranstalterRepository veranstalterRepo, final AktuelleTeilnahmeService teilnahmenService) {

		SchulenOverviewService result = new SchulenOverviewService();
		result.aktuelleTeilnahmeService = teilnahmenService;
		result.veranstalterRepository = veranstalterRepo;
		return result;

	}

	public List<SchuleAPIModel> ermittleAnmeldedatenFuerSchulen(final Identifier lehrerID) {

		List<SchuleAPIModel> items = new ArrayList<>();

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(lehrerID);

		if (optVeranstalter.isEmpty()) {

			String msg = "Unzulässiger Zugriff auf Schulen durch UUID " + lehrerID
				+ ": es gibt keinen Veranstalter mit dieser UUID.";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);

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

	/**
	 * Zum Nachladen der Schuldetails wird die Schule nochmal benötigt.
	 *
	 * @param  lehrerID
	 * @param  schulkuerzel
	 * @return              SchuleAPIModel
	 */
	public SchuleAPIModel ermittleSchuleMitKuerzelFuerLehrer(final Identifier lehrerID, final String schulkuerzel) {

		List<SchuleAPIModel> schulen = this.ermittleAnmeldedatenFuerSchulen(lehrerID);

		Optional<SchuleAPIModel> optSchule = schulen.stream().filter(s -> s.kuerzel().equals(schulkuerzel)).findFirst();

		if (optSchule.isEmpty()) {

			String msg = "Unzulässige Abfrage der Schule " + schulkuerzel + " durch UUID " + lehrerID
				+ ": der Lehrer ist nicht für diese Schule registriert.";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);

			throw new NotFoundException();
		}
		return optSchule.get();
	}

	SecurityIncidentRegistered getSecurityIncidentRegistered() {

		return securityIncidentRegistered;
	}
}
