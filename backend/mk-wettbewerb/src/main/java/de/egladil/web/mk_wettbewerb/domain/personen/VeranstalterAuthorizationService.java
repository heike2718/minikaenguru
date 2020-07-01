// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.error.AccessDeniedException;
import de.egladil.web.mk_wettbewerb.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_wettbewerb.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;

/**
 * VeranstalterAuthorizationService
 */
@ApplicationScoped
@DomainService
public class VeranstalterAuthorizationService {

	private static final Logger LOG = LoggerFactory.getLogger(VeranstalterAuthorizationService.class);

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	Event<SecurityIncidentRegistered> securityIncidentEvent;

	private SecurityIncidentRegistered securityIncidentRegistered;

	public static VeranstalterAuthorizationService createForTest(final VeranstalterRepository repo) {

		VeranstalterAuthorizationService service = new VeranstalterAuthorizationService();
		service.veranstalterRepository = repo;
		return service;
	}

	/**
	 * @param  veranstalterID
	 * @param  teilnahmeID
	 * @return                       boolean - nicht void wegen Mockito.
	 * @throws AccessDeniedException
	 */
	public boolean checkPermissionForTeilnahmenummer(final Identifier veranstalterID, final Identifier teilnahmeID) throws AccessDeniedException {

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(veranstalterID);

		if (optVeranstalter.isEmpty()) {

			String msg = "Unzulaessiger Zugriff auf Teilnahme " + teilnahmeID + " durch " + veranstalterID
				+ ": Veranstalter existiert nicht";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new AccessDeniedException();
		}

		Optional<Identifier> optTeilnahmeIdentifier = optVeranstalter.get().teilnahmeIdentifier().stream()
			.filter(tID -> tID.equals(teilnahmeID)).findFirst();

		if (optTeilnahmeIdentifier.isEmpty()) {

			String msg = "Unzulaessiger Zugriff auf Teilnahmenummer " + teilnahmeID + " durch " + veranstalterID
				+ ": Veranstalter existiert, aber hat keine Berechtigung.";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new AccessDeniedException();
		}

		return true;
	}

	SecurityIncidentRegistered getSecurityIncidentRegistered() {

		return securityIncidentRegistered;
	}
}
