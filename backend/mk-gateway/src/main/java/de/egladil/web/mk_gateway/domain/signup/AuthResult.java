// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

/**
 * AuthResult ist das AuthResult vom SignUp / LogIn.
 */
public class AuthResult {

	private long expiresAt;

	private String state;

	@NotBlank
	private String nonce;

	@NotBlank
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

	@Override
	public int hashCode() {

		return Objects.hash(idToken);
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
		AuthResult other = (AuthResult) obj;
		return Objects.equals(idToken, other.idToken);
	}

}
