// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * AuthorizationService
 */
@ApplicationScoped
@DomainService
public class AuthorizationService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationService.class);

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	UserRepository userRepository;

	@Inject
	Event<SecurityIncidentRegistered> securityIncidentEvent;

	private SecurityIncidentRegistered securityIncidentRegistered;

	public static AuthorizationService createForTest(final VeranstalterRepository veranstalterRepository, final UserRepository userRepository) {

		AuthorizationService service = new AuthorizationService();
		service.userRepository = userRepository;
		service.veranstalterRepository = veranstalterRepository;
		return service;
	}

	/**
	 * @param  veranstalterID
	 * @param  teilnahmeID
	 * @return                       boolean - nicht void wegen Mockito.
	 * @throws AccessDeniedException
	 */
	public boolean checkPermissionForTeilnahmenummer(final Identifier veranstalterID, final Identifier teilnahmeID) throws AccessDeniedException {

		Optional<User> optUser = userRepository.ofId(veranstalterID.identifier());

		if (optUser.isEmpty()) {

			String msg = "Unzulaessiger Zugriff auf Teilnahme " + teilnahmeID + " durch " + veranstalterID
				+ ": User existiert nicht";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new AccessDeniedException();
		}

		User user = optUser.get();

		if (Rolle.ADMIN.equals(user.getRolle())) {

			return true;

		}

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

			String msg = "Unzulaessiger Zugriff auf Teilnahmenummer " + teilnahmeID + " durch " + optVeranstalter.get()
				+ ": Veranstalter hat keine Berechtigung.";

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
