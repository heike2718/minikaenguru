// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb.api;

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
	private String status;

	@JsonProperty
	@NotBlank
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

	@JsonProperty
	private String loesungsbuchstabenIkids;

	@JsonProperty
	private String loesungsbuchstabenKlasse1;

	@JsonProperty
	private String loesungsbuchstabenKlasse2;

	public static EditWettbewerbModel createForTest(final int jahr, final String beginn, final String ende, final String freischaltungLehrer, final String freischaltungPrivat) {

		EditWettbewerbModel result = new EditWettbewerbModel();

		result.jahr = jahr;
		result.wettbewerbsbeginn = beginn;
		result.wettbewerbsende = ende;
		result.datumFreischaltungLehrer = freischaltungLehrer;
		result.datumFreischaltungPrivat = freischaltungPrivat;
		return result;

	}

	public static EditWettbewerbModel createForTest(final int jahr, final String beginn, final String ende, final String freischaltungLehrer, final String freischaltungPrivat, final String loesungsbuchstabenIkids, final String loesungsbuchstabenKlasse1, final String loesungsbuchstabenKlasse2) {

		EditWettbewerbModel result = EditWettbewerbModel.createForTest(jahr, beginn, ende, freischaltungLehrer,
			freischaltungPrivat);

		result.loesungsbuchstabenIkids = loesungsbuchstabenIkids;
		result.loesungsbuchstabenKlasse1 = loesungsbuchstabenKlasse1;
		result.loesungsbuchstabenKlasse2 = loesungsbuchstabenKlasse2;
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

	public String getLoesungsbuchstabenIkids() {

		return loesungsbuchstabenIkids;
	}

	public String getLoesungsbuchstabenKlasse1() {

		return loesungsbuchstabenKlasse1;
	}

	public String getLoesungsbuchstabenKlasse2() {

		return loesungsbuchstabenKlasse2;
	}
}
