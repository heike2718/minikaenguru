// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.WettbewerbStatus;

/**
 * WettbewerbListAPIModel
 */
public class WettbewerbListAPIModel {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private WettbewerbStatus status;

	@JsonProperty
	private boolean completelyLoaded = false;

	public static WettbewerbListAPIModel fromWettbewerb(final Wettbewerb wettbewerb) {

		WettbewerbListAPIModel result = new WettbewerbListAPIModel();
		result.jahr = wettbewerb.id().jahr().intValue();
		result.status = wettbewerb.status();
		return result;
	}

}
