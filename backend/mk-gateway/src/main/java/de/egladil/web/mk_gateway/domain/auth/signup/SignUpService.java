// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.signup;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.auth.events.LehrerCreated;
import de.egladil.web.mk_gateway.domain.auth.events.PrivatveranstalterCreated;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.SynchronizeVeranstalterService;
import de.egladil.web.mk_gateway.domain.veranstalter.api.CreateUserCommand;
import de.egladil.web.mk_gateway.infrastructure.messaging.PropagateUserService;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * SignUpService
 */
@RequestScoped
public class SignUpService {

	private static final Logger LOG = LoggerFactory.getLogger(SignUpService.class);

	@Inject
	UserRepository userRepository;

	@Inject
	SynchronizeVeranstalterService syncVeranstalterService;

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	PropagateUserService propagateUserService;

	private MkGatewayDomainEvent event;

	/**
	 * Legt einen neuen Minikänguru-Veranstalter an.
	 *
	 * @param  command
	 * @return         User
	 */
	public User verifySyncTokenAndCreateUser(final CreateUserCommand command) {

		String syncToken = command.getSyncToken();
		this.syncVeranstalterService.verifySession(syncToken);
		this.syncVeranstalterService.removeSyncToken(syncToken);

		SignUpResourceOwner signUpResourceOwner = new SignUpResourceOwner(command.getUuid(), command.getFullName(),
			command.getEmail(), command.getNonce());

		return this.createUserAndVeranstalter(signUpResourceOwner);

	}

	@Transactional
	User createUserAndVeranstalter(final SignUpResourceOwner signUpResourceOwner) {

		if (signUpResourceOwner == null) {

			throw new IllegalArgumentException("signUpResourceOwner darf nicht null sein");
		}

		final String uuid = signUpResourceOwner.uuid();
		Optional<User> optUser = userRepository.ofId(uuid);

		if (optUser.isPresent()) {

			LOG.debug("User mit UUID={} bereits vorhanden", uuid);
			return optUser.get();
		}

		final String fullName = signUpResourceOwner.fullName();

		Rolle rolle = signUpResourceOwner.rolle();

		switch (rolle) {

		case LEHRER:
			final String schulkuerzel = signUpResourceOwner.schulkuerzel();
			event = new LehrerCreated(CommonTimeUtils.now(), uuid, fullName, signUpResourceOwner.email(), schulkuerzel,
				signUpResourceOwner.isNewsletterEmpfaenger());
			break;

		case PRIVAT:
			event = new PrivatveranstalterCreated(CommonTimeUtils.now(), uuid, fullName, signUpResourceOwner.email(),
				signUpResourceOwner.isNewsletterEmpfaenger());
			break;

		default:
			throw new MkGatewayRuntimeException("resourceOwner hat die falsche Rolle " + rolle);
		}

		User user = new User();
		user.setImportierteUuid(uuid);
		user.setRolle(rolle);

		userRepository.addUser(user);
		// #322: seit ich das wegen der Transaktionslogik nicht mehr mit dem async-Event-Mechanismus behandle,
		propagateUserService.handleDomainEvent(event);

		domainEventHandler.handleEvent(event);

		return user;
	}

	MkGatewayDomainEvent event() {

		return event;
	}
}
