// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.apimodel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.Wettbewerb;

/**
 * WettbewerbDetailsAPIModel
 */
public class WettbewerbDetailsAPIModel {

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

	@JsonProperty
	private TeilnahmenuebersichtAPIModel teilnahmenuebersicht;

	@JsonProperty
	private boolean completelyLoaded = true;

	public static WettbewerbDetailsAPIModel fromWettbewerb(final Wettbewerb wettbewerb) {

		WettbewerbDetailsAPIModel result = new WettbewerbDetailsAPIModel();

		result.jahr = wettbewerb.id().jahr().intValue();
		result.status = wettbewerb.status().toString();
		result.wettbewerbsbeginn = CommonTimeUtils.format(wettbewerb.wettbewerbsbeginn());
		result.wettbewerbsende = CommonTimeUtils.format(wettbewerb.wettbewerbsende());
		result.datumFreischaltungLehrer = CommonTimeUtils.format(wettbewerb.datumFreischaltungLehrer());
		result.datumFreischaltungPrivat = CommonTimeUtils.format(wettbewerb.datumFreischaltungPrivat());
		return result;
	}

	public WettbewerbDetailsAPIModel withTeilnahmenuebersicht(final TeilnahmenuebersichtAPIModel teilnahmenubersicht) {

		this.teilnahmenuebersicht = teilnahmenubersicht;
		return this;
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