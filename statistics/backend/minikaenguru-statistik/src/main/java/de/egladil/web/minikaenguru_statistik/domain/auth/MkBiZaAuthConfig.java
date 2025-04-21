// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.auth;

import io.smallrye.config.ConfigMapping;

/**
 * MkBiZaAuthConfig
 */
@ConfigMapping(prefix = "mkbiza.auth")
public interface MkBiZaAuthConfig {

	String client();

	String header();

}
