// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.error.AccessDeniedException;
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

	public void checkPermissionForTeilnahmenummer(final Identifier veranstalterID, final Identifier teilnahmeID) throws AccessDeniedException {

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(veranstalterID);

		if (optVeranstalter.isEmpty()) {

			LOG.warn("Veranstalter {} nicht vorhanden", veranstalterID);
			throw new AccessDeniedException();
		}

		Optional<Identifier> optTeilnahmeIdentifier = optVeranstalter.get().teilnahmeIdentifier().stream()
			.filter(tID -> tID.equals(teilnahmeID)).findFirst();

		if (optTeilnahmeIdentifier.isEmpty()) {

			LOG.warn("Veranstalter {} hat keine Berechtigung für die Teilnahmenummer {}", optVeranstalter.get(), teilnahmeID);
			throw new AccessDeniedException();
		}
	}
}
