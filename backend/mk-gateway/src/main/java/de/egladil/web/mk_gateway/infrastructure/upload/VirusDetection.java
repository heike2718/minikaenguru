// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.upload;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * VirusDetection
 */
public class VirusDetection {

	private static final String KEY_VIRUS_DETECTED = "virusDetected";

	private static final String KEY_SCANNER_MESSAGE = "scannerMessage";

	@JsonProperty
	private final boolean virusDetected;

	@JsonProperty
	private String scannerMessage;

	public static final VirusDetection TRUE = new VirusDetection(true);

	public static final VirusDetection FALSE = new VirusDetection(false);

	public static VirusDetection withAttributes(final Map<String, Object> keyValueMap) {

		if (keyValueMap.get(KEY_VIRUS_DETECTED) != null) {

			Boolean isVirus = (Boolean) keyValueMap.get(KEY_VIRUS_DETECTED);

			if (isVirus) {

				if (keyValueMap.get(KEY_SCANNER_MESSAGE) != null) {

					String msg = (String) keyValueMap.get(KEY_SCANNER_MESSAGE);

					return TRUE.withScannerMessage(msg);
				}

				return TRUE;
			}
		}

		return FALSE;

	}

	private VirusDetection() {

		virusDetected = false;
	}

	/**
	 * @param virusDetected
	 */
	private VirusDetection(final boolean virusDetected) {

		this.virusDetected = virusDetected;
	}

	@Override
	public String toString() {

		return "VirusDetection [virusDetected=" + virusDetected + ", scannerMessage=" + scannerMessage + "]";
	}

	public boolean isVirusDetected() {

		return virusDetected;
	}

	public String getScannerMessage() {

		return scannerMessage;
	}

	private VirusDetection withScannerMessage(final String scannerMessage) {

		this.scannerMessage = scannerMessage;
		return this;
	}
}
