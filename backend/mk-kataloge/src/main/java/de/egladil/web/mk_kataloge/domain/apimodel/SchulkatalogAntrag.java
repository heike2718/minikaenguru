// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.StringLatin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

	public SchulkatalogAntrag withEmail(final String email) {

		this.email = email;
		return this;
	}

	public SchulkatalogAntrag withLand(final String land) {

		this.land = land;
		return this;
	}

	public SchulkatalogAntrag withOrt(final String ort) {

		this.ort = ort;
		return this;
	}

	public SchulkatalogAntrag withPlz(final String plz) {

		this.plz = plz;
		return this;
	}

	public SchulkatalogAntrag withSchulname(final String schulname) {

		this.schulname = schulname;
		return this;
	}

	public SchulkatalogAntrag withStrasseUndHausnummer(final String strasseUndHausnummer) {

		this.strasseUndHausnummer = strasseUndHausnummer;
		return this;
	}

	public SchulkatalogAntrag withKleber(final String kleber) {

		this.kleber = kleber;
		return this;
	}

}
