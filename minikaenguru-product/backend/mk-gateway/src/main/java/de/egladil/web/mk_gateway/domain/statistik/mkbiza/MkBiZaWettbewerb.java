// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;

/**
 * MkBiZaWettbewerb
 */
public class MkBiZaWettbewerb {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private WettbewerbStatus status;

	public MkBiZaWettbewerb() {

		super();

	}

	public MkBiZaWettbewerb(final int jahr, final WettbewerbStatus status) {

		super();
		this.jahr = jahr;
		this.status = status;
	}

}
