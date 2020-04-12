// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.domain.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.jwt.Claims;
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
import de.egladil.web.mkv_api_gateway.domain.services.UserRepository;
import de.egladil.web.mkv_api_gateway.error.AuthException;
import de.egladil.web.mkv_api_gateway.error.LogmessagePrefixes;
import de.egladil.web.mkv_api_gateway.error.MkvApiGatewayRuntimeException;
import de.egladil.web.mkv_api_gateway.error.PendingRegistrationException;
import de.egladil.web.mkv_api_gateway.infrastructure.persistence.entities.User;

/**
 * MkvApiSessionService
 */
@ApplicationScoped
public class MkvApiSessionService {

	private static final Logger LOG = LoggerFactory.getLogger(MkvApiSessionService.class);

	private static final int SESSION_IDLE_TIMEOUT_MINUTES = 120;

	@Inject
	JWTService jwtService;

	@Inject
	UserRepository userRepository;

	@Inject
	CryptoService cryptoService;

	private ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

	/**
	 * @param  sessionId
	 * @return
	 */
	public Session getAndRefreshSessionIfValid(final String sessionId, final String path) {

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

			DecodedJWT decodedJWT = jwtService.verify(jwt, getPublicKey());

			String uuid = decodedJWT.getSubject();
			String fullName = Claims.full_name.name();

			Optional<User> optUser = userRepository.ofId(uuid);

			if (optUser.isEmpty()) {

				throw new PendingRegistrationException();
			}

			byte[] sessionIdBase64 = Base64.getEncoder().encode(cryptoService.generateSessionId().getBytes());
			String sessionId = new String(sessionIdBase64);

			User user = optUser.get();

			LoggedInUser loggedInUser = LoggedInUser.create(uuid, user.getRolle(), fullName,
				CommonHttpUtils.createUserIdReference());

			Session session = Session.create(sessionId, loggedInUser);
			session.setExpiresAt(SessionUtils.getExpiresAt(SESSION_IDLE_TIMEOUT_MINUTES));

			sessions.put(sessionId, session);

			return session;
		} catch (TokenExpiredException e) {

			LOG.error("JWT expired");
			throw new AuthException("JWT has expired");
		} catch (JWTVerificationException e) {

			LOG.warn(LogmessagePrefixes.BOT + "JWT invalid: {}", e.getMessage());
			throw new AuthException("invalid JWT");
		}

	}

	private byte[] getPublicKey() {

		try (InputStream in = getClass().getResourceAsStream("/META-INF/authprov_public_key.pem");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName("UTF-8"));

			return sw.toString().getBytes();
		} catch (IOException e) {

			throw new MkvApiGatewayRuntimeException("Konnte jwt-public-key nicht lesen: " + e.getMessage());
		}

	}

	/**
	 * @param sessionId
	 */
	public void invalidateSession(final String sessionId) {

		Session session = sessions.remove(sessionId);

		if (session != null) {

			LOG.info("Session invalidated: {} - {}", sessionId, session.user().uuid().substring(0, 8));
		}

	}
}
