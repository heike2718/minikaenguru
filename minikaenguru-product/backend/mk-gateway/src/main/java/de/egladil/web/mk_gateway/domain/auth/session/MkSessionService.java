// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.session;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.mk_gateway.domain.DecodedJWTReader;
import de.egladil.web.mk_gateway.domain.auth.events.UserLoggedIn;
import de.egladil.web.mk_gateway.domain.auth.events.UserLoggedOut;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.error.LogmessagePrefixes;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * MkSessionService
 */
@ApplicationScoped
public class MkSessionService {

	private static final Logger LOG = LoggerFactory.getLogger(MkSessionService.class);

	private static final int SESSION_IDLE_TIMEOUT_MINUTES = 120;

	@Inject
	JWTService jwtService;

	@Inject
	UserRepository userRepository;

	@Inject
	CryptoService cryptoService;

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	LoggableEventDelegate eventDelegate;

	private ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

	/**
	 * @param  sessionId
	 * @return           Session
	 */
	public Session getAndRefreshSessionIfValid(final String sessionId) {

		Session session = sessions.get(sessionId);

		if (session == null) {

			return null;
		}

		LocalDateTime expireDateTime = CommonTimeUtils.transformFromDate(new Date(session.getExpiresAt()));
		LocalDateTime now = CommonTimeUtils.now();

		if (now.isAfter(expireDateTime)) {

			sessions.remove(sessionId);
			throw new SessionExpiredException("Ihre Session ist abgelaufen. Bitte loggen Sie sich erneut ein.");
		}

		session.setExpiresAt(SessionUtils.getExpiresAt(SESSION_IDLE_TIMEOUT_MINUTES));

		return session;
	}

	/**
	 * @param  jwt
	 * @return
	 */
	public Session initSession(final String jwt) {

		try {

			DecodedJWT decodedJWT = jwtService.verify(jwt, SessionUtils.getPublicKey());

			String uuid = decodedJWT.getSubject();

			String fullName = new DecodedJWTReader(decodedJWT).getFullName();

			Optional<User> optUser = userRepository.ofId(uuid);

			if (optUser.isEmpty()) {

				String msg = "USER mit UUID " + uuid + " existiert nicht";

				eventDelegate.fireSecurityEvent(msg, domainEventHandler);
				throw new AuthException(msg);
			}

			byte[] sessionIdBase64 = Base64.getEncoder().encode(cryptoService.generateSessionId().getBytes());
			String sessionId = new String(sessionIdBase64);

			User user = optUser.get();

			String userIdReference = CommonHttpUtils.createUserIdReference() + "_" + user.getUuid().substring(0, 8);
			LoggedInUser loggedInUser = LoggedInUser.create(uuid, user.getRolle(), fullName,
				userIdReference);

			Session session = Session.create(sessionId, loggedInUser);
			session.setExpiresAt(SessionUtils.getExpiresAt(SESSION_IDLE_TIMEOUT_MINUTES));

			sessions.put(sessionId, session);

			UserLoggedIn userLoggedIn = new UserLoggedIn(uuid, user.getRolle());

			if (domainEventHandler != null) {

				domainEventHandler.handleEvent(userLoggedIn);
			} else {

				System.out.println(userLoggedIn.serializeQuietly());
			}

			return session;
		} catch (TokenExpiredException e) {

			LOG.error("JWT expired");
			throw new AuthException("JWT expired");
		} catch (JWTVerificationException e) {

			String msg = LogmessagePrefixes.BOT + "JWT " + StringUtils.abbreviate(jwt, 20) + " invalid: " + e.getMessage();

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);

			LOG.warn(msg);
			throw new AuthException("JWT invalid");
		}

	}

	/**
	 * @param sessionId
	 */
	public void invalidateSession(final String sessionId) {

		Session session = sessions.remove(sessionId);

		if (session != null) {

			LOG.debug("Session invalidated: {} - {}", sessionId, session.user().uuid().substring(0, 8));

			UserLoggedOut userLoggedOut = new UserLoggedOut(session.user().uuid(), session.user().rolle());

			if (domainEventHandler != null) {

				domainEventHandler.handleEvent(userLoggedOut);
			} else {

				System.out.println(userLoggedOut.serializeQuietly());
			}
		} else {

			LOG.info("session was null");
		}

	}

	void initSessionForTest(final Session session) {

		sessions.put(session.sessionId(), session);
	}
}
