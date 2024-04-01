// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.auth;

import java.util.Base64;

/**
 * BaseAuthHeaderUtils
 */
public class BaseAuthHeaderUtils {

	public static String getSecretBase64(final String secret) {

		return new String(Base64.getEncoder().encode(secret.getBytes()));
	}
}
