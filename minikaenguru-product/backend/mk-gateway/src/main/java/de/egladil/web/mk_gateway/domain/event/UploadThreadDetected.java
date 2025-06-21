// =====================================================
// Project: filescanner-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UploadThreadDetected
 */
public class UploadThreadDetected extends AbstractDomainEvent {

	@JsonProperty
	private String clientId;

	@JsonProperty
	private String ownerId;

	@JsonProperty
	private String fileName;

	@JsonProperty
	private String filescannerMessage;

	@Override
	@JsonIgnore
	public String typeName() {

		return EventType.UPLOAD_THREAD_DETECTED.getLabel();
	}

	public UploadThreadDetected withOwnerId(final String ownerId) {

		this.ownerId = ownerId;
		return this;
	}

	public UploadThreadDetected withFileName(final String fileName) {

		this.fileName = fileName;
		return this;
	}

	public UploadThreadDetected withClientId(final String clientId) {

		this.clientId = StringUtils.abbreviate(clientId, 11);
		return this;
	}

	public UploadThreadDetected withFilescannerMessage(final String filescannerMessage) {

		this.filescannerMessage = filescannerMessage;
		return this;
	}

}
