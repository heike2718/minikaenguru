// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.wettbewerbe;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wettbewerb
 */
public class Wettbewerb {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private StatusWettbewerb status;

}
