// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.scan;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FileScanResult
 */
@Schema(description = "Ergebnisse des Scans")
public class FileScanResult {

	@JsonProperty
	private String userID;

	@JsonProperty
	private String uploadName;

	@JsonProperty
	private String mediaType;

	@JsonProperty
	private VirusDetection virusDetection;

	@JsonProperty
	private ThreadDetection threadDetection;

	public String getMediaType() {

		return mediaType;
	}

	public FileScanResult withMediaType(final String mediaType) {

		this.mediaType = mediaType;
		return this;
	}

	public VirusDetection getVirusDetection() {

		return virusDetection;
	}

	public FileScanResult withVirusDetection(final VirusDetection virusDetection) {

		this.virusDetection = virusDetection;
		return this;
	}

	public String getUserID() {

		return userID;
	}

	public FileScanResult withUserID(final String userID) {

		this.userID = userID;
		return this;
	}

	public String getUploadName() {

		return uploadName;
	}

	public FileScanResult withUploadName(final String uploadName) {

		this.uploadName = uploadName;
		return this;
	}

	public ThreadDetection getThreadDetection() {

		return threadDetection;
	}

	public FileScanResult withThreadDetection(final ThreadDetection threadDetection) {

		this.threadDetection = threadDetection;
		return this;
	}

}
