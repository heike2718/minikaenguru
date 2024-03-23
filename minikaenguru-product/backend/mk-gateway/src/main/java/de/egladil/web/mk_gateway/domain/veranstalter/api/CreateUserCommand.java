// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CreateUserCommand
 */
public class CreateUserCommand {

	@JsonProperty
	private String syncToken;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	@JsonProperty
	private String email;

	@JsonProperty
	private String nonce;

	@JsonProperty
	private String clientId;

	@Override
	public String toString() {

		return "CreateUserCommand [uuid=" + StringUtils.abbreviate(uuid, 11) + ", fullName=" + fullName + ", email="
			+ StringUtils.abbreviate(email, 8) + ", nonce=" + nonce + "]";
	}

	public String getSyncToken() {

		return syncToken;
	}

	public void setSyncToken(final String syncToken) {

		this.syncToken = syncToken;
	}

	public String getUuid() {

		return uuid;
	}

	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}

	public String getFullName() {

		return fullName;
	}

	public void setFullName(final String fullName) {

		this.fullName = fullName;
	}

	public String getEmail() {

		return email;
	}

	public void setEmail(final String email) {

		this.email = email;
	}

	public String getNonce() {

		return nonce;
	}

	public void setNonce(final String nonce) {

		this.nonce = nonce;
	}

	public String getClientId() {

		return clientId;
	}

	public void setClientId(final String abbreviatedClientId) {

		this.clientId = abbreviatedClientId;
	}

}
