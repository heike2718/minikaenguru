// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.upload;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ScanRequestPayload
 */
public class ScanRequestPayload {

	@JsonProperty
	private String clientId;

	@JsonProperty
	private String fileOwner;

	@JsonProperty
	private Upload upload;

	public String getClientId() {

		return clientId;
	}

	public ScanRequestPayload withClientId(final String clientId) {

		this.clientId = clientId;
		return this;
	}

	public String getFileOwner() {

		return fileOwner;
	}

	public ScanRequestPayload withFileOwner(final String fileOwner) {

		this.fileOwner = fileOwner;
		return this;
	}

	public Upload getUpload() {

		return upload;
	}

	public ScanRequestPayload withUpload(final Upload upload) {

		this.upload = upload;
		return this;
	}
}
