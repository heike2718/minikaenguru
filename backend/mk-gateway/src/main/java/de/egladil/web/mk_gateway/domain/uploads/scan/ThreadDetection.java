// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.scan;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ThreadDetection
 */
public class ThreadDetection {

	@JsonProperty
	private boolean securityThreadDetected;

	@JsonProperty
	private String securityCheckMessage;

	public String getSecurityCheckMessage() {

		return securityCheckMessage;
	}

	public ThreadDetection withSecurityCheckMessage(final String securityCheckMessage) {

		this.securityCheckMessage = securityCheckMessage;
		return this;
	}

	public boolean isSecurityThreadDetected() {

		return securityThreadDetected;
	}

	public ThreadDetection withSecurityThreadDetected(final boolean securityThreadDetected) {

		this.securityThreadDetected = securityThreadDetected;
		return this;
	}
}
