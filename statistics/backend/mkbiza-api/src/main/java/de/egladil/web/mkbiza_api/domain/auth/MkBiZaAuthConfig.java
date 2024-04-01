// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.auth;

import io.smallrye.config.ConfigMapping;

/**
 * MkBiZaAuthConfig
 */
@ConfigMapping(prefix = "mkbiza.auth")
public interface MkBiZaAuthConfig {

	String client();

	String header();

}
