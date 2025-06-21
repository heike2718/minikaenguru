// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.auth;

import io.smallrye.config.ConfigMapping;

/**
 * MinikaenguruStatistikAuthConfig
 */
@ConfigMapping(prefix = "mks.auth")
public interface MinikaenguruStatistikAuthConfig {

	String client();

	String header();

}
