// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.veranstalter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Hausnummer;
import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.Plz;
import de.egladil.web.commons_validation.annotations.StringLatin;

/**
 * VertragAdvAPIModel
 */
public class VertragAdvAPIModel {

	@JsonProperty
	@Kuerzel
	private String schulkuerzel;

	@NotBlank
	@StringLatin
	@Size(min = 1, max = 100)
	@JsonProperty
	private String schulname;

	@NotBlank
	@Size(min = 1, max = 10)
	@Plz
	@JsonProperty
	private String plz;

	@NotBlank
	@StringLatin
	@Size(min = 1, max = 100)
	@JsonProperty
	private String ort;

	@NotBlank
	@Size(min = 1, max = 100)
	@StringLatin
	@JsonProperty
	private String strasse;

	@NotBlank
	@Size(min = 1, max = 10)
	@Hausnummer
	@JsonProperty
	private String hausnummer;

	public String schulkuerzel() {

		return schulkuerzel;
	}

	public String schulname() {

		return schulname;
	}

	public String plz() {

		return plz;
	}

	public String ort() {

		return ort;
	}

	public String strasse() {

		return strasse;
	}

	public String hausnummer() {

		return hausnummer;
	}

	@Override
	public String toString() {

		return "VertragAdvAPIModel [schulkuerzel=" + schulkuerzel + ", schulname=" + schulname
			+ ", ort=" + ort + "]";
	}
}
