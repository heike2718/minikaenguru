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
 * WettbewerbAPIModel
 */
public class WettbewerbAPIModel {

	@JsonProperty
	private String jahr;

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

	public static WettbewerbAPIModel fromWettbewerb(final Wettbewerb wettbewerb) {

		WettbewerbAPIModel result = new WettbewerbAPIModel();

		result.jahr = wettbewerb.id().toString();
		result.status = wettbewerb.status();
		result.wettbewerbsbeginn = CommonTimeUtils.transformFromLocalDate(wettbewerb.wettbewerbsbeginn());
		result.wettbewerbsende = CommonTimeUtils.transformFromLocalDate(wettbewerb.wettbewerbsende());
		result.datumFreischaltungLehrer = CommonTimeUtils.transformFromLocalDate(wettbewerb.datumFreischaltungLehrer());
		result.datumFreischaltungPrivat = CommonTimeUtils.transformFromLocalDate(wettbewerb.datumFreischaltungPrivat());
		return result;
	}

}
