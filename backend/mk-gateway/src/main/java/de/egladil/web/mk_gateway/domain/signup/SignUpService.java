// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.services.UserRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * SignUpService
 */
@RequestScoped
public class SignUpService {

	private static final Logger LOG = LoggerFactory.getLogger(SignUpService.class);

	@Inject
	UserRepository userRepository;

	public Optional<User> findUser(final String uuid) {

		return this.userRepository.ofId(uuid);
	}

	public User signUpUser() {

		return null;
	}

}
