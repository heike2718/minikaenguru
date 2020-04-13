// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

/**
 * AuthResult ist das AuthResult vom SignUp / LogIn.
 */
public class AuthResult {

	private long expiresAt;

	private String state;

	private String nonce;

	private String idToken;

	public long getExpiresAt() {

		return expiresAt;
	}

	public void setExpiresAt(final long expiresAt) {

		this.expiresAt = expiresAt;
	}

	public String getState() {

		return state;
	}

	public void setState(final String state) {

		this.state = state;
	}

	public String getNonce() {

		return nonce;
	}

	public void setNonce(final String nonce) {

		this.nonce = nonce;
	}

	public String getIdToken() {

		return idToken;
	}

	public void setIdToken(final String idToken) {

		this.idToken = idToken;
	}

}
