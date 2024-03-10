// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * GuiVersionService
 */
@ApplicationScoped
public class GuiVersionService {

	@ConfigProperty(name = "quarkus.application.version")
	String version;

	/**
	 * Gibt die GUI-Version zurück. Das sind immer die 3 ersten Versionszahlen Major.Middle.Minor.
	 *
	 * @return String
	 */
	public String getExcpectedGuiVersion() {

		String[] tokens = version.split("\\.");
		return tokens[0] + "." + tokens[1] + "." + tokens[2];
	}
}
