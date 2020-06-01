// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.apimodel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * EditWettbewerbModel zum Anlegen und Ändern eines Wettbewerbs
 */
public class EditWettbewerbModel {

	@JsonProperty
	private int jahr;

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

	public static EditWettbewerbModel create(final int jahr, final String beginn, final String ende, final String freischaltungLehrer, final String freischaltungPrivat) {

		EditWettbewerbModel result = new EditWettbewerbModel();

		result.jahr = jahr;
		result.wettbewerbsbeginn = beginn;
		result.wettbewerbsende = ende;
		result.datumFreischaltungLehrer = freischaltungLehrer;
		result.datumFreischaltungPrivat = freischaltungPrivat;
		return result;

	}

	public int getJahr() {

		return jahr;
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
