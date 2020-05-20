// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SchuleAPIModel ist das, was eine API, die sich für eine Schule als Katalogeintrag interessiert, sieht.
 */
public class SchuleAPIModel {

	@JsonProperty
	private String kuerzel;

	@JsonProperty
	private String name;

	@JsonProperty
	private String ort;

	@JsonProperty
	private String land;

	SchuleAPIModel() {

	}

	public SchuleAPIModel(final String kuerzel, final String name, final String ort, final String land) {

		this.kuerzel = kuerzel;
		this.name = name;
		this.ort = ort;
		this.land = land;
	}

}
