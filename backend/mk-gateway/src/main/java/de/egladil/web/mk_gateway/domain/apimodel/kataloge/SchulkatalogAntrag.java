// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.kataloge;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.StringLatin;

/**
 * SchulkatalogAntrag
 */
public class SchulkatalogAntrag {

	@NotNull
	@Email
	@JsonProperty
	private String email;

	@StringLatin
	@NotBlank
	@JsonProperty
	private String land;

	@StringLatin
	@NotBlank
	@JsonProperty
	private String ort;

	@StringLatin
	@JsonProperty
	private String plz;

	@StringLatin
	@NotBlank
	@JsonProperty
	private String schulname;

	@StringLatin
	@JsonProperty
	private String strasseUndHausnummer;

	@JsonProperty
	private String kleber;

	public static SchulkatalogAntrag createForTest(final String email, final String land, final String ort, final String plz, final String schulname, final String strasse, final String kleber) {

		SchulkatalogAntrag result = new SchulkatalogAntrag();
		result.email = email;
		result.kleber = kleber;
		result.land = land;
		result.ort = ort;
		result.plz = plz;
		result.schulname = schulname;
		result.strasseUndHausnummer = strasse;
		return result;
	}

	public String email() {

		return email;
	}

	public String land() {

		return land;
	}

	public String ort() {

		return ort;
	}

	public String plz() {

		return plz;
	}

	public String schulname() {

		return schulname;
	}

	public String strasseUndHausnummer() {

		return strasseUndHausnummer;
	}

	public String kleber() {

		return kleber;
	}

	public String toSecurityLog() {

		return "SchulkatalogAntrag [email=" + email + ", land=" + land + ", ort=" + ort + ", plz=" + plz + ", schulname="
			+ schulname + ", strasseUndHausnummer=" + strasseUndHausnummer + ", kleber=" + kleber + "]";
	}

}
