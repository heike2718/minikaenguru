// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.teilnahmen;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SchuleDto
 */
public class SchuleDto {

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

	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public String getOrt() {

		return ort;
	}

	public void setOrt(final String ort) {

		this.ort = ort;
	}

	public String getLand() {

		return land;
	}

	public void setLand(final String land) {

		this.land = land;
	}

	public String getKuerzelLand() {

		return kuerzelLand;
	}

	public void setKuerzelLand(final String kuerzelLand) {

		this.kuerzelLand = kuerzelLand;
	}
}
