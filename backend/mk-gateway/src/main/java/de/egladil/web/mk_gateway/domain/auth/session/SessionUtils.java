// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import jakarta.ws.rs.core.NewCookie;

/**
 * SessionUtils
 */
public final class SessionUtils {

	/**
	 *
	 */
	private SessionUtils() {

	}

	public static byte[] getPublicKey() {

		try (InputStream in = SessionUtils.class.getResourceAsStream("/META-INF/authprov_public_key.pem");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName(MkGatewayFileUtils.DEFAULT_ENCODING));

			return sw.toString().getBytes();
		} catch (IOException e) {

			throw new MkGatewayRuntimeException("Konnte jwt-public-key nicht lesen: " + e.getMessage());
		}

	}

	public static String createIdReference() {

		return "" + UUID.randomUUID().getMostSignificantBits();
	}

	/**
	 * Berechnet den expiresAt-Zeitpunkt mit dem gegebenen idle timout.
	 *
	 * @param  sessionIdleTimeoutMinutes
	 *                                   int Anzahl Minuten, nach denen eine Session als idle weggeräumt wird.
	 * @return                           long
	 */
	public static long getExpiresAt(final int sessionIdleTimeoutMinutes) {

		return CommonTimeUtils.getInterval(CommonTimeUtils.now(), sessionIdleTimeoutMinutes,
			ChronoUnit.MINUTES).getEndTime().getTime();
	}

	public static NewCookie createSessionCookie(final String cookieName, final String sessionId) {

		// @formatter:off
		return new NewCookie.Builder(cookieName)
				.value(sessionId)
				.path("/")
				.domain(null)
				.comment(null)
				.maxAge(360000) // maximum age of the cookie in seconds
				.httpOnly(true)
				.secure(true).build();
		// @formatter:on

	}

}
