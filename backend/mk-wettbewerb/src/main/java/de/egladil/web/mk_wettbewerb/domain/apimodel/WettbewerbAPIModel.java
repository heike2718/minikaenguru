// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;

/**
 * WettbewerbAPIModel
 */
public class WettbewerbAPIModel {

	@JsonProperty
	private String jahr;

	@JsonProperty
	private String wettbewerbsende;

	@JsonProperty
	private String datumFreischaltungLehrer;

	@JsonProperty
	private String datumFreischaltungPrivat;

	public static WettbewerbAPIModel fromWettbewerb(final Wettbewerb wettbewerb) {

		WettbewerbAPIModel result = new WettbewerbAPIModel();

		result.jahr = wettbewerb.id().toString();
		result.wettbewerbsende = CommonTimeUtils.format(wettbewerb.wettbewerbsende());
		result.datumFreischaltungLehrer = CommonTimeUtils.format(wettbewerb.datumFreischaltungLehrer());
		result.datumFreischaltungPrivat = CommonTimeUtils.format(wettbewerb.datumFreischaltungPrivat());
		return result;

	}

}
