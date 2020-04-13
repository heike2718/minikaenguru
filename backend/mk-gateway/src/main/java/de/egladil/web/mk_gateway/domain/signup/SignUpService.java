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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.model.DomainEvent;
import de.egladil.web.mk_gateway.domain.model.Rolle;
import de.egladil.web.mk_gateway.domain.services.JwtDecoderService;
import de.egladil.web.mk_gateway.domain.services.UserRepository;
import de.egladil.web.mk_gateway.error.AuthException;
import de.egladil.web.mk_gateway.error.LogmessagePrefixes;
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
	JWTService jwtService;

	@Inject
	Event<DomainEvent> createdEvent;

	public Optional<User> findUser(final String uuid) {

		return this.userRepository.ofId(uuid);
	}

	public User signUpUser() {

		return null;
	}

	@Transactional
	public void createUser(final AuthResult authResult) {

		String jwt = authResult.getIdToken();
		JwtDecoderService decoderService = new JwtDecoderService();

		try {

			DecodedJWT decodedJWT = decoderService.decodeJwt(jwt, jwtService);

			String uuid = decodedJWT.getSubject();
			Optional<User> optUser = userRepository.ofId(uuid);

			if (optUser.isPresent()) {

				return;
			}

			String fullName = decoderService.getFullName(decodedJWT);
			Rolle rolle = null;
			String schulkuerzel = null;
			DomainEvent event = null;

			if (authResult.getNonce().contains("LEHRER")) {

				rolle = Rolle.LEHRER;
				schulkuerzel = authResult.getNonce().split("-")[1];

				event = new LehrerCreated(CommonTimeUtils.now(), uuid, fullName, schulkuerzel);

			} else {

				rolle = Rolle.PRIVAT;
				event = new PrivatmenschCreated(CommonTimeUtils.now(), uuid, fullName);
			}

			User user = new User();
			user.setImportierteUuid(uuid);
			user.setRolle(rolle);

			userRepository.addUser(user);

			createdEvent.fire(event);
		} catch (TokenExpiredException e) {

			LOG.error("JWT expired");
			throw new AuthException("JWT has expired");
		} catch (JWTVerificationException e) {

			LOG.warn(LogmessagePrefixes.BOT + "JWT invalid: {}", e.getMessage());
			throw new AuthException("invalid JWT");
		}

	}

}
