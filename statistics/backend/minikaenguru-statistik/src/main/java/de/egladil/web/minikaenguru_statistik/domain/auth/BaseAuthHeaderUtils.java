// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.auth;

import java.util.Base64;

/**
 * BaseAuthHeaderUtils
 */
public class BaseAuthHeaderUtils {

	public static String getSecretBase64(final String secret) {

		return new String(Base64.getEncoder().encode(secret.getBytes()));
	}
}
