// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.session;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.commons_crypto.impl.CryptoServiceImpl;
import de.egladil.web.commons_crypto.impl.JWTServiceImpl;
import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.mk_gateway.domain.DecodedJWTReader;
import de.egladil.web.mk_gateway.domain.error.AuthException;
import de.egladil.web.mk_gateway.domain.error.LogmessagePrefixes;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
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
	Event<SecurityIncidentRegistered> securityEvent;

	@Inject
	Event<UserLoggedIn> loginEvent;

	@Inject
	Event<UserLoggedOut> logoutEvent;

	private SecurityIncidentRegistered securityIncident;

	private UserLoggedIn loginEventObject;

	private UserLoggedOut logoutEventObject;

	private ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

	static MkSessionService createForTestWithUserRepository(final UserRepository userRepository) {

		MkSessionService result = new MkSessionService();
		result.jwtService = new JWTServiceImpl();
		result.userRepository = userRepository;
		result.cryptoService = new CryptoServiceImpl();
		return result;
	}

	static MkSessionService createForTestWithSession(final Session session) {

		MkSessionService result = new MkSessionService();
		result.jwtService = new JWTServiceImpl();
		result.cryptoService = new CryptoServiceImpl();

		result.sessions.put(session.sessionId(), session);
		return result;

	}

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

				this.securityIncident = new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);
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

			this.loginEventObject = new UserLoggedIn(uuid, user.getRolle());

			if (loginEvent != null) {

				loginEvent.fire(loginEventObject);
			}

			return session;
		} catch (TokenExpiredException e) {

			LOG.error("JWT expired");
			throw new AuthException("JWT expired");
		} catch (JWTVerificationException e) {

			String msg = LogmessagePrefixes.BOT + "JWT " + StringUtils.abbreviate(jwt, 20) + " invalid: " + e.getMessage();

			this.securityIncident = new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);

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

			logoutEventObject = new UserLoggedOut(session.user().uuid(), session.user().rolle());

			if (logoutEvent != null) {

				logoutEvent.fire(logoutEventObject);
			}
		} else {

			LOG.info("session was null");
		}

	}

	SecurityIncidentRegistered getSecurityIncident() {

		return securityIncident;
	}

	UserLoggedIn getLoginEventObject() {

		return loginEventObject;
	}

	UserLoggedOut getLogoutEventObject() {

		return logoutEventObject;
	}
}
