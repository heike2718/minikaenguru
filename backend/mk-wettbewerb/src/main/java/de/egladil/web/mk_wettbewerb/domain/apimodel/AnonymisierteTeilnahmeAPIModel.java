// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;

/**
 * AnonymisierteTeilnahmeAPIModel
 */
public class AnonymisierteTeilnahmeAPIModel {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private Teilnahmeart teilnahmeart;

}
