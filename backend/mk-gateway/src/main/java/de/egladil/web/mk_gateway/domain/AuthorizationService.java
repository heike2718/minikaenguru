// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

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
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UserHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VeranstalterHibernateRepository;

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

	public static AuthorizationService createForIntegrationTests(final EntityManager entityManager) {

		AuthorizationService result = new AuthorizationService();
		result.veranstalterRepository = VeranstalterHibernateRepository.createForIntegrationTest(entityManager);
		result.userRepository = UserHibernateRepository.createForIntegrationTest(entityManager);
		return result;
	}

	/**
	 * @param  userIdentifier
	 * @param  identifierTeilnahmenummer
	 * @param  kontext
	 *                                   String
	 * @return                           boolean - nicht void wegen Mockito.
	 * @throws AccessDeniedException
	 */
	public boolean checkPermissionForTeilnahmenummer(final Identifier userIdentifier, final Identifier identifierTeilnahmenummer, final String kontext) throws AccessDeniedException {

		Optional<User> optUser = userRepository.ofId(userIdentifier.identifier());

		if (optUser.isEmpty()) {

			String msg = kontext + ": unzulaessiger Zugriff mit Teilnahmenummer " + identifierTeilnahmenummer + " durch "
				+ userIdentifier
				+ ": User existiert nicht";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new AccessDeniedException();
		}

		User user = optUser.get();

		if (Rolle.ADMIN.equals(user.getRolle())) {

			return true;

		}

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(userIdentifier);

		if (optVeranstalter.isEmpty()) {

			String msg = kontext + ": unzulaessiger Zugriff mit Teilnahmenummer " + identifierTeilnahmenummer + " durch "
				+ userIdentifier
				+ ": Veranstalter existiert nicht";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);
			throw new AccessDeniedException();
		}

		Optional<Identifier> optTeilnahmeIdentifier = optVeranstalter.get().teilnahmeIdentifier().stream()
			.filter(tID -> tID.equals(identifierTeilnahmenummer)).findFirst();

		if (optTeilnahmeIdentifier.isEmpty()) {

			String msg = kontext + ": unzulaessiger Zugriff mit Teilnahmenummer " + identifierTeilnahmenummer + " durch "
				+ optVeranstalter.get()
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
