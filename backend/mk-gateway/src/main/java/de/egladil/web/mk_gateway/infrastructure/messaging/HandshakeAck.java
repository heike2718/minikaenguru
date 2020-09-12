// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

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

}
