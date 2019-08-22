//=====================================================
// Projekt: mkadmin-server
// (c) Heike Winkelvoß
//=====================================================

package de.egladil.web.mkadmin_server.config;

import de.egladil.web.commons.config.DynamicConfiguration;

/**
 * DynamicConfigProperties
 */
public class DynamicConfigProperties implements DynamicConfiguration {

	private String mkadminServerVersion;

	public String getMkadminServerVersion() {
		return mkadminServerVersion;
	}

	public void setMkadminServerVersion(final String checklistenappVersion) {
		this.mkadminServerVersion = checklistenappVersion;
	}
}
