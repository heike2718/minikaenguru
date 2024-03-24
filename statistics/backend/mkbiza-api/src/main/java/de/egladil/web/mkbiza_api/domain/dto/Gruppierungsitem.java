// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Gruppierungsitem
 */
public class Gruppierungsitem {

	@JsonProperty
	private String name;

	@JsonProperty
	private int anzahl;

}
