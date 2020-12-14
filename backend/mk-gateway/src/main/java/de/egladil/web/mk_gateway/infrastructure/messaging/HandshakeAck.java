// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HandshakeAck
 */
public class HandshakeAck {

	@JsonProperty
	private String syncToken;

	@JsonProperty
	private String nonce;

	HandshakeAck() {

	}

	public HandshakeAck(final String syncToken, final String nonce) {

		this.syncToken = syncToken;
		this.nonce = nonce;
	}

	public String syncToken() {

		return syncToken;
	}

	public String nonce() {

		return nonce;
	}

	@Override
	public int hashCode() {

		return Objects.hash(syncToken);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		HandshakeAck other = (HandshakeAck) obj;
		return Objects.equals(syncToken, other.syncToken);
	}

}
