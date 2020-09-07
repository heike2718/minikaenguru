// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.veranstalter;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ChangeUserCommand
 */
public class ChangeUserCommand {

	@JsonProperty
	private String sendingClientId;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String vorname;

	@JsonProperty
	private String nachname;

	@JsonProperty
	private String email;

	ChangeUserCommand() {

	}

	public String sendingClientId() {

		return sendingClientId;
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

}
