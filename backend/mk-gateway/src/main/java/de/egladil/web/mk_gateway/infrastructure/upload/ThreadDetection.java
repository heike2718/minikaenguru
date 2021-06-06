// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.upload;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ThreadDetection
 */
public class ThreadDetection {

	private static final String KEY_THRAD_DETECTED = "securityThreadDetected";

	private static final String KEY_MESSAGE = "securityCheckMessage";

	@JsonProperty
	private final boolean securityThreadDetected;

	@JsonProperty
	private String securityCheckMessage;

	public static final ThreadDetection TRUE = new ThreadDetection(true);

	public static final ThreadDetection FALSE = new ThreadDetection(false);

	public static ThreadDetection withAttributes(final Map<String, Object> keyValueMap) {

		if (keyValueMap.get(KEY_THRAD_DETECTED) != null) {

			Boolean isDetected = (Boolean) keyValueMap.get(KEY_THRAD_DETECTED);

			if (isDetected) {

				if (keyValueMap.get(KEY_MESSAGE) != null) {

					String msg = (String) keyValueMap.get(KEY_MESSAGE);

					return TRUE.withSecurityCheckMessage(msg);
				}

				return TRUE;
			}
		}

		return FALSE;
	}

	private ThreadDetection(final boolean securityThreadDetected) {

		this.securityThreadDetected = securityThreadDetected;
	}

	public String getSecurityCheckMessage() {

		return securityCheckMessage;
	}

	private ThreadDetection withSecurityCheckMessage(final String securityCheckMessage) {

		this.securityCheckMessage = securityCheckMessage;
		return this;
	}

	public boolean isSecurityThreadDetected() {

		return securityThreadDetected;
	}
}
