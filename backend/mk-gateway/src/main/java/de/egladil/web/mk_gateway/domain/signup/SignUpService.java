// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * SignUpService
 */
@RequestScoped
public class SignUpService {

	@Inject
	UserRepository userRepository;

	@Inject
	Event<MkGatewayDomainEvent> createdEvent;

	private MkGatewayDomainEvent event;

	public static SignUpService createForTest(final UserRepository userRepository) {

		SignUpService result = new SignUpService();
		result.userRepository = userRepository;
		return result;
	}

	@Transactional
	public User createUser(final SignUpResourceOwner signUpResourceOwner) {

		if (signUpResourceOwner == null) {

			throw new IllegalArgumentException("signUpResourceOwner darf nicht null sein");
		}

		final String uuid = signUpResourceOwner.uuid();
		Optional<User> optUser = userRepository.ofId(uuid);

		if (optUser.isPresent()) {

			return optUser.get();
		}

		final String fullName = signUpResourceOwner.fullName();

		Rolle rolle = signUpResourceOwner.rolle();

		switch (rolle) {

		case LEHRER:
			final String schulkuerzel = signUpResourceOwner.schulkuerzel();
			event = new LehrerCreated(CommonTimeUtils.now(), uuid, fullName, schulkuerzel);
			break;

		case PRIVAT:
			event = new PrivatmenschCreated(CommonTimeUtils.now(), uuid, fullName);
			break;

		default:
			throw new MkGatewayRuntimeException("resourceOwner hat die falsche Rolle " + rolle);
		}

		User user = new User();
		user.setImportierteUuid(uuid);
		user.setRolle(rolle);

		userRepository.addUser(user);

		if (createdEvent != null) {

			createdEvent.fire(event);
		}

		return user;
	}

	MkGatewayDomainEvent event() {

		return event;
	}
}
