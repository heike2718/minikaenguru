// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.apimodel;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.WettbewerbStatus;

/**
 * WettbewerbDetailsAPIModel
 */
public class WettbewerbDetailsAPIModel {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private WettbewerbStatus status;

	@JsonProperty
	private Date wettbewerbsbeginn;

	@JsonProperty
	private Date wettbewerbsende;

	@JsonProperty
	private Date datumFreischaltungLehrer;

	@JsonProperty
	private Date datumFreischaltungPrivat;

	@JsonProperty
	private TeilnahmenuebersichAPIModel teilnahmenuebersicht;

	@JsonProperty
	private boolean completelyLoaded = true;

	public static WettbewerbDetailsAPIModel fromWettbewerb(final Wettbewerb wettbewerb) {

		WettbewerbDetailsAPIModel result = new WettbewerbDetailsAPIModel();

		result.jahr = wettbewerb.id().jahr().intValue();
		result.status = wettbewerb.status();
		result.wettbewerbsbeginn = CommonTimeUtils.transformFromLocalDate(wettbewerb.wettbewerbsbeginn());
		result.wettbewerbsende = CommonTimeUtils.transformFromLocalDate(wettbewerb.wettbewerbsende());
		result.datumFreischaltungLehrer = CommonTimeUtils.transformFromLocalDate(wettbewerb.datumFreischaltungLehrer());
		result.datumFreischaltungPrivat = CommonTimeUtils.transformFromLocalDate(wettbewerb.datumFreischaltungPrivat());
		return result;
	}

	public WettbewerbDetailsAPIModel withTeilnahmenuebersicht(final TeilnahmenuebersichAPIModel teilnahmenubersicht) {

		this.teilnahmenuebersicht = teilnahmenubersicht;
		return this;
	}

}
