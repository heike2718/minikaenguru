// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ChangeUserCommand
 */
public class ChangeUserCommand {

	@JsonProperty
	private String syncToken;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String vorname;

	@JsonProperty
	private String nachname;

	@JsonProperty
	private String email;

	public String syncToken() {

		return syncToken;
	}

	public String uuid() {

		return uuid;
	}

	public String fullName() {

		return vorname + " " + nachname;
	}

	public String email() {

		return email;
	}

	@Override
	public String toString() {

		return "ChangeUserCommand [uuid=" + StringUtils.abbreviate(uuid, 11) + ", vorname=" + vorname + ", nachname=" + nachname
			+ ", email="
			+ StringUtils.abbreviate(email, 8) + "]";
	}

	public ChangeUserCommand withSyncToken(final String syncToken) {

		this.syncToken = syncToken;
		return this;
	}

	public ChangeUserCommand withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public ChangeUserCommand withVorname(final String vorname) {

		this.vorname = vorname;
		return this;
	}

	public ChangeUserCommand withNachname(final String nachname) {

		this.nachname = nachname;
		return this;
	}

	public ChangeUserCommand withEmail(final String email) {

		this.email = email;
		return this;
	}

}
