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

	@JsonProperty
	private String kuerzelLand;

	SchuleAPIModel() {

	}

	public SchuleAPIModel(final String kuerzel, final String name, final String ort, final String land, final String kuerzelLand) {

		this.kuerzel = kuerzel;
		this.name = name;
		this.ort = ort;
		this.land = land;
		this.kuerzelLand = kuerzelLand;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public String getName() {

		return name;
	}

	public String getOrt() {

		return ort;
	}

	public String getLand() {

		return land;
	}

	public String getKuerzelLand() {

		return kuerzelLand;
	}

}
