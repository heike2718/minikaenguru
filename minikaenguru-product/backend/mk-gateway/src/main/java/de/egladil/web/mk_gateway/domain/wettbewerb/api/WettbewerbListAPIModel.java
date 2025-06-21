// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;

/**
 * WettbewerbListAPIModel
 */
public class WettbewerbListAPIModel {

	@JsonProperty
	private Integer jahr;

	@JsonProperty
	private WettbewerbStatus status;

	@JsonProperty
	private boolean completelyLoaded = false;

	public static WettbewerbListAPIModel fromWettbewerb(final Wettbewerb wettbewerb) {

		WettbewerbListAPIModel result = new WettbewerbListAPIModel();
		result.jahr = wettbewerb.id().jahr();
		result.status = wettbewerb.status();
		return result;
	}

	public Integer jahr() {

		return jahr;
	}

	public WettbewerbStatus status() {

		return status;
	}

	public boolean completelyLoaded() {

		return completelyLoaded;
	}

}
