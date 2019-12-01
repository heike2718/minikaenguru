// =====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkadmin_server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.NewCookie;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.jwt.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.mkadmin_server.MkadminServerApp;
import de.egladil.web.mkadmin_server.domain.UserSession;
import de.egladil.web.mkadmin_server.error.AuthException;
import de.egladil.web.mkadmin_server.error.LogmessagePrefixes;
import de.egladil.web.mkadmin_server.error.MkadminRuntimeException;

/**
 * MkadminSessionService
 */
@ApplicationScoped
public class MkadminSessionService {

	private static final Logger LOG = LoggerFactory.getLogger(MkadminSessionService.class);

	private static final int SESSION_IDLE_TIMEOUT_MINUTES = 60;

	private Map<String, UserSession> sessions = new ConcurrentHashMap<>();

	@Inject
	CryptoService cryptoService;

	@Inject
	JWTService jwtService;

	public UserSession createUserSession(final String jwt) {

		try {

			DecodedJWT decodedJWT = jwtService.verify(jwt, getPublicKey());

			String uuid = decodedJWT.getSubject();

			Claim groupsClaim = decodedJWT.getClaim(Claims.groups.name());
			String[] rolesArr = groupsClaim.asArray(String.class);

			if (rolesArr != null) {

				Optional<String> optAdminRole = Arrays.stream(rolesArr).filter(str -> "ADMIN".equals(str)).findFirst();

				if (optAdminRole.isEmpty()) {

					LOG.info("Login-Versuch durch uuid={} mit Rollen {}, erlaubt ist nur die Rolle ADMIN",
						StringUtils.abbreviate(uuid, 11),
						StringUtils.join(rolesArr));
					throw new AuthException();
				}
			}

			byte[] sessionIdBase64 = Base64.getEncoder().encode(cryptoService.generateSessionId().getBytes());
			String sesionId = new String(sessionIdBase64);

			UserSession userSession = UserSession.create(sesionId, CommonHttpUtils.createUserIdReference(), uuid);
			userSession.setExpiresAt(getSessionTimeout());

			sessions.put(sesionId, userSession);

			return userSession;

		} catch (TokenExpiredException e) {

			LOG.warn("JWT expired");
			throw new AuthException();
		} catch (JWTVerificationException e) {

			LOG.warn(LogmessagePrefixes.BOT + "JWT invalid: {}", e.getMessage());
			throw new AuthException();
		}
	}

	public void invalidate(final String sessionId) {

		UserSession userSession = sessions.remove(sessionId);

		if (userSession != null) {

			LOG.info("Session invalidated: {} - {}", sessionId, userSession.getUuid().substring(0, 8));
		}

	}

	public NewCookie createSessionCookie(final String sessionId) {

		final String name = MkadminServerApp.CLIENT_COOKIE_PREFIX + CommonHttpUtils.NAME_SESSIONID_COOKIE;

		LOG.debug("Erzeugen Cookie mit name={}", name);

		// @formatter:off
		NewCookie sessionCookie = new NewCookie(name,
			sessionId,
			"/", // path
			null, // domain muss null sein, wird vom Browser anhand des restlichen Responses abgeleitet. Sonst wird das Cookie nicht gesetzt.
			1,  // version
			null, // comment
			7200, // expires (minutes)
			null,
			true, // secure
			true  // httpOnly
			);
		// @formatter:on

		return sessionCookie;
	}

	private byte[] getPublicKey() {

		try (InputStream in = getClass().getResourceAsStream("/META-INF/authprov_public_key.pem");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName("UTF-8"));

			return sw.toString().getBytes();
		} catch (IOException e) {

			throw new MkadminRuntimeException("Konnte jwt-public-key nicht lesen: " + e.getMessage());
		}

	}

	private long getSessionTimeout() {

		return CommonTimeUtils.getInterval(CommonTimeUtils.now(), SESSION_IDLE_TIMEOUT_MINUTES,
			ChronoUnit.MINUTES).getEndTime().getTime();
	}
}
