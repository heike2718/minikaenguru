// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.teilnahmen;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AnonymisierteTeilnahmeAPIModel
 */
public class AnonymisierteTeilnahmeAPIModel {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private String teilnahmeart;

	@JsonProperty
	private int anzahlKinder;

	AnonymisierteTeilnahmeAPIModel() {

	}

	public int jahr() {

		return jahr;
	}

	public String teilnahmenummer() {

		return teilnahmenummer;
	}

	public String teilnahmeart() {

		return teilnahmeart;
	}

	public int anzahlKinder() {

		return anzahlKinder;
	}
}
