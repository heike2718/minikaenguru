// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.client;

/**
 * AccessTokenWithNonce
 */
public class AccessTokenWithNonce {

	private String nonce;

	private String accessToken;

	public String getNonce() {

		return nonce;
	}

	public String getAccessToken() {

		return accessToken;
	}

}
