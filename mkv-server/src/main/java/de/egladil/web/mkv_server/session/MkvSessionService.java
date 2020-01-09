// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.session;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
import de.egladil.web.mk_commons.dao.IUserDao;
import de.egladil.web.mk_commons.domain.impl.User;
import de.egladil.web.mk_commons.exception.AuthException;
import de.egladil.web.mk_commons.exception.LogmessagePrefixes;
import de.egladil.web.mk_commons.session.Session;
import de.egladil.web.mk_commons.session.SessionUser;
import de.egladil.web.mk_commons.session.SessionUtils;

/**
 * MkvSessionService
 */
@ApplicationScoped
public class MkvSessionService {

	private static final Logger LOG = LoggerFactory.getLogger(MkvSessionService.class);

	private static final int SESSION_IDLE_TIMEOUT_MINUTES = 60;

	private Map<String, Session> sessions = new ConcurrentHashMap<>();

	@Inject
	IUserDao userDao;

	@Inject
	CryptoService cryptoService;

	@Inject
	JWTService jwtService;

	public Session initSession(final String jwt) {

		try {

			DecodedJWT decodedJWT = jwtService.verify(jwt, SessionUtils.getPublicKey());

			String uuid = decodedJWT.getSubject();

			Optional<User> optUser = userDao.findByUUId(uuid);

			String abbreviatedUuid = StringUtils.abbreviate(uuid, 11);

			if (optUser.isEmpty()) {

				LOG.warn("kein USER mit uuid={} bekannt hier.", abbreviatedUuid);
				throw new AuthException();
			}

			User user = optUser.get();
			String sessionId = createSessionId();
			String idReference = SessionUtils.createIdReference();

			SessionUser sessionUser = SessionUser.create(sessionId, user.getRolle().name(), idReference);
			Session session = Session.create(sessionId, sessionUser);
			session.setExpiresAt(SessionUtils.getExpiresAt(SESSION_IDLE_TIMEOUT_MINUTES));

			sessions.put(sessionId, session);

			// TODO hier in die DB das Login-Ereignis eintragen
			LOG.info("User {} eingeloggt", abbreviatedUuid);

			return session;
		} catch (TokenExpiredException e) {

			LOG.warn("JWT expired");
			throw new AuthException();

		} catch (JWTVerificationException e) {

			LOG.warn(LogmessagePrefixes.BOT + "JWT invalid: {}", e.getMessage());
			throw new AuthException();
		}
	}

	public void invalidateSession(final String sessionId) {

		Session removedSession = sessions.remove(sessionId);

		if (removedSession != null) {

			String abbreviatedUuid = StringUtils.abbreviate(removedSession.getUser().getUuid(), 11);
			// TODO hier in die DB das Logout-Ereignis eintragen
			LOG.info("User {} ausgeloggt", abbreviatedUuid);
		}

	}

	/**
	 * Sucht die Session mit der gegebenen sessionId. Falls sie noch gültig ist, wird sie um die SESION_IDLE_TIMEOUT- Zeit
	 * verlängert.
	 *
	 * @param  sessionId
	 * @return                         Session oder null
	 * @throws SessionExpiredException
	 *                                 falls die Session nicht mehr gültig ist.
	 */
	public Session getAndRefreshSessionIfValid(final String sessionId) throws SessionExpiredException {

		Session session = sessions.get(sessionId);

		if (session == null) {

			return null;
		}

		if (session.getUser() == null) {

			LOG.error("Sesssion ohne User!!!");
			sessions.remove(sessionId);
			throw new SessionExpiredException("Ihre Session ist abgelaufen. Bitte loggen Sie sich erneut ein.");
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

	private String createSessionId() {

		byte[] sessionIdBase64 = Base64.getEncoder().encode(cryptoService.generateSessionId().getBytes());
		String sessionId = new String(sessionIdBase64);
		return sessionId;
	}

}
