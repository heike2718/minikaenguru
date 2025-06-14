// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Ort;
import jakarta.validation.constraints.NotBlank;

/**
 * OrtPayload
 */
public class OrtPayload {

	@JsonProperty
	@Kuerzel
	@NotBlank
	private String kuerzel;

	@JsonProperty
	@StringLatin
	@NotBlank
	private String name;

	@JsonProperty
	@Kuerzel
	@NotBlank
	private String kuerzelLand;

	@JsonProperty
	@StringLatin
	@NotBlank
	private String nameLand;

	/**
	 * @param  ort
	 * @return
	 */
	public static OrtPayload create(final Ort ort) {

		OrtPayload result = new OrtPayload();
		result.kuerzel = ort.getKuerzel();
		result.name = ort.getName();
		result.kuerzelLand = ort.getLandKuerzel();
		result.nameLand = ort.getLandName();
		return result;
	}

	public static OrtPayload create(@NotBlank final String kuerzel, @NotBlank final String name, @NotBlank final String kuerzelLand, @NotBlank final String nameLand) {

		OrtPayload result = new OrtPayload();
		result.kuerzel = kuerzel;
		result.name = name;
		result.kuerzelLand = kuerzelLand;
		result.nameLand = nameLand;
		return result;
	}

	public String kuerzel() {

		return kuerzel;
	}

	public String name() {

		return name;
	}

	public String kuerzelLand() {

		return kuerzelLand;
	}

	public String nameLand() {

		return nameLand;
	}

	@Override
	public String toString() {

		return "OrtPayload [kuerzel=" + kuerzel + ", name=" + name + ", kuerzelLand=" + kuerzelLand + ", nameLand=" + nameLand
			+ "]";
	}

}
