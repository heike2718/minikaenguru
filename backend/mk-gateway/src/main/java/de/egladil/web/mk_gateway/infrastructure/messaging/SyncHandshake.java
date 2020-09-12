// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SyncHandshake
 */
public class SyncHandshake {

	@JsonProperty
	private String sendingClientId;

	@JsonProperty
	private String nonce;

	@JsonIgnore
	private long expiresAt;

	SyncHandshake() {

	}

	public String sendingClientId() {

		return sendingClientId;
	}

	public String nonce() {

		return nonce;
	}

	public long getExpiresAt() {

		return expiresAt;
	}

	public void setExpiresAt(final long expiresAt) {

		this.expiresAt = expiresAt;
	}

}
