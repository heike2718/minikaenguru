// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.apimodel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * WettbewerbAPIModel zum Anlegen und Ändern eines Wettbewerbs
 */
public class WettbewerbAPIModel {

	@JsonProperty
	private int jahr;

	@JsonProperty
	@NotBlank
	private String status;

	@JsonProperty
	@Pattern(regexp = "\\d{2}.\\d{2}.\\d{4}")
	private String wettbewerbsbeginn;

	@JsonProperty
	@NotBlank
	@Pattern(regexp = "\\d{2}.\\d{2}.\\d{4}")
	private String wettbewerbsende;

	@JsonProperty
	@NotBlank
	@Pattern(regexp = "\\d{2}.\\d{2}.\\d{4}")
	private String datumFreischaltungLehrer;

	@JsonProperty
	@NotBlank
	@Pattern(regexp = "\\d{2}.\\d{2}.\\d{4}")
	private String datumFreischaltungPrivat;

	public static WettbewerbAPIModel create(final int jahr, final String status, final String beginn, final String ende, final String freischaltungLehre, final String freischaltungPrivat) {

		WettbewerbAPIModel result = new WettbewerbAPIModel();

		result.jahr = jahr;
		result.status = status;
		result.wettbewerbsbeginn = beginn;
		result.wettbewerbsende = ende;
		result.datumFreischaltungLehrer = freischaltungLehre;
		result.datumFreischaltungPrivat = freischaltungPrivat;
		return result;

	}

	public int getJahr() {

		return jahr;
	}

	public String getStatus() {

		return status;
	}

	public String getWettbewerbsbeginn() {

		return wettbewerbsbeginn;
	}

	public String getWettbewerbsende() {

		return wettbewerbsende;
	}

	public String getDatumFreischaltungLehrer() {

		return datumFreischaltungLehrer;
	}

	public String getDatumFreischaltungPrivat() {

		return datumFreischaltungPrivat;
	}
}
