// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel.migration;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.personen.Rolle;;

/**
 * ZuMigrierenderVeranstalter
 */
public class ZuMigrierenderVeranstalter implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = 1L;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String rolle;

	@JsonProperty
	private String fullName;

	@JsonProperty
	private boolean mailbenachrichtigung;

	@JsonProperty
	private String schulkuerzel;

	@Override
	public String toString() {

		return "Daten [uuid=" + uuid + ", rolle=" + rolle + ", fullName=" + fullName
			+ ", mailbenachrichtigung=" + mailbenachrichtigung + ", schulkuerzel=" + schulkuerzel + "]";
	}

	public String uuid() {

		return uuid;
	}

	public Rolle rolle() throws IllegalArgumentException {

		return Rolle.valueOf(rolle);
	}

	public String fullName() {

		return fullName;
	}

	public boolean mailbenachrichtigung() {

		return mailbenachrichtigung;
	}

	public String schulkuerzel() {

		return schulkuerzel;
	}
}
