// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.apimodel;

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
	private String strasse;

	@StringLatin
	@JsonProperty
	private String hausnummer;

	@JsonProperty
	private String honeypot;

	public static SchulkatalogAntrag createForTest(final String email, final String land, final String ort, final String plz, final String schulname, final String strasse, final String hausnummer, final String honeypot) {

		SchulkatalogAntrag result = new SchulkatalogAntrag();
		result.email = email;
		result.hausnummer = hausnummer;
		result.honeypot = honeypot;
		result.land = land;
		result.ort = ort;
		result.plz = plz;
		result.schulname = schulname;
		result.strasse = strasse;
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

	public String strasse() {

		return strasse;
	}

	public String hausnummer() {

		return hausnummer;
	}

	public String honeypot() {

		return honeypot;
	}

	public String toSecurityLog() {

		return "SchulkatalogAntrag [email=" + email + ", land=" + land + ", ort=" + ort + ", plz=" + plz + ", schulname="
			+ schulname + ", strasse=" + strasse + ", hausnummer=" + hausnummer + ", honeypot=" + honeypot + "]";
	}

}
