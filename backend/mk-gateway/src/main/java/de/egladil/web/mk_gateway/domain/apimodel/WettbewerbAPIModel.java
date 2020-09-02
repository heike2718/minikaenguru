// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;

/**
 * WettbewerbAPIModel
 */
public class WettbewerbAPIModel {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private String wettbewerbsbeginn;

	@JsonProperty
	private String wettbewerbsende;

	@JsonProperty
	private String datumFreischaltungLehrer;

	@JsonProperty
	private String datumFreischaltungPrivat;

	@JsonProperty
	private WettbewerbStatus status;

	public static WettbewerbAPIModel fromWettbewerb(final Wettbewerb wettbewerb) {

		WettbewerbAPIModel result = new WettbewerbAPIModel();

		result.jahr = wettbewerb.id().jahr().intValue();
		result.wettbewerbsbeginn = CommonTimeUtils.format(wettbewerb.wettbewerbsbeginn());
		result.wettbewerbsende = CommonTimeUtils.format(wettbewerb.wettbewerbsende());
		result.datumFreischaltungLehrer = CommonTimeUtils.format(wettbewerb.datumFreischaltungLehrer());
		result.datumFreischaltungPrivat = CommonTimeUtils.format(wettbewerb.datumFreischaltungPrivat());
		result.status = wettbewerb.status();
		return result;

	}

}
