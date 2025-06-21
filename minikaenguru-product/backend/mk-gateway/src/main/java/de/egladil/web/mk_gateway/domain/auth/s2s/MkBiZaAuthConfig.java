// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.s2s;

import io.smallrye.config.ConfigMapping;

/**
 * MkBiZaAuthConfig gruppiert die Konfigurationsparameter mit dem Präfix mkbiza.auth
 */
@ConfigMapping(prefix = "mkbiza.auth")
public interface MkBiZaAuthConfig {

	String client();

	String header();
}
