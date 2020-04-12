// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.ws.rs.core.NewCookie;

import org.apache.commons.io.IOUtils;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.error.MkvApiGatewayRuntimeException;

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

			IOUtils.copy(in, sw, Charset.forName("UTF-8"));

			return sw.toString().getBytes();
		} catch (IOException e) {

			throw new MkvApiGatewayRuntimeException("Konnte jwt-public-key nicht lesen: " + e.getMessage());
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
		NewCookie sessionCookie = new NewCookie(cookieName,
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

}
