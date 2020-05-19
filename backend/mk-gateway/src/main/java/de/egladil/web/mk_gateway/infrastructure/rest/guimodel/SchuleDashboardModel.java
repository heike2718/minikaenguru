// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.guimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SchuleDashboardModel
 */
public class SchuleDashboardModel {

	@JsonProperty
	private PersonInfo[] kollegen; // mk-wettbewerb

	@JsonProperty
	private PersonInfo angemeldetDurch; // mk-wettbewerb

	@JsonProperty
	private int anzahlTeilnahmen; // mk-wettbewerb

}
