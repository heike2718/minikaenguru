// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.migration;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * ZuMigrierenderBenutzer
 */
@Deprecated(forRemoval = true)
public class ZuMigrierenderBenutzer implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = 1L;

	@JsonProperty
	private String secret;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String rolle;

	@JsonProperty
	private String email;

	@JsonProperty
	private String loginname;

	@JsonProperty
	private String vorname;

	@JsonProperty
	private String nachname;

	@JsonProperty
	private String pw;

	@JsonProperty
	private String alg;

	@JsonProperty
	private String slt;

	@JsonProperty
	private int rounds;

	@JsonProperty
	private boolean mailbenachrichtigung;

	@JsonProperty
	private String schulkuerzel;

	@JsonProperty
	private boolean anonym;

	public String uuid() {

		return uuid;
	}

	public Rolle rolle() throws IllegalArgumentException {

		return Rolle.valueOf(rolle);
	}

	public String fullName() {

		return vorname + " " + nachname;
	}

	public boolean mailbenachrichtigung() {

		return mailbenachrichtigung;
	}

	public String schulkuerzel() {

		return schulkuerzel;
	}

	public String getSecret() {

		return secret;
	}

	public boolean isAnonym() {

		return anonym;
	}
}
