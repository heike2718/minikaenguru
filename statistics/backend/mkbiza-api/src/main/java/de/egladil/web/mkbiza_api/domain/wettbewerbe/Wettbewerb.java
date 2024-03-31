// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.wettbewerbe;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkbiza_api.domain.dto.Gruppierungsitem;

/**
 * Wettbewerb
 */
public class Wettbewerb {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private StatusWettbewerb status;

	@JsonProperty
	private List<Gruppierungsitem> medianeJeKlassenstufe;

}
