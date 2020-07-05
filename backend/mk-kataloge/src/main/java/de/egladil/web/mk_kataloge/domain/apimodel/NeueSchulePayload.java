// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.apimodel;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;

/**
 * NeueSchulePayload
 */
public class NeueSchulePayload {

	@JsonProperty
	@StringLatin
	@NotBlank
	private String name;

	@JsonProperty
	@Kuerzel
	@NotBlank
	private String kuerzel;

	@JsonProperty
	@Kuerzel
	@NotBlank
	private String kuerzelOrt;

	@JsonProperty
	@StringLatin
	@NotBlank
	private String nameOrt;

	@JsonProperty
	@Kuerzel
	@NotBlank
	private String kuerzelLand;

	@JsonProperty
	@StringLatin
	@NotBlank
	private String nameLand;

	public static NeueSchulePayload create(final String name, final String kuerzel, final String kuerzelOrt, final String nameOrt, final String kuerzelLand, final String nameLand) {

		NeueSchulePayload result = new NeueSchulePayload();
		result.name = name;
		result.kuerzel = kuerzel;
		result.kuerzelOrt = kuerzelOrt;
		result.nameOrt = nameOrt;
		result.kuerzelLand = kuerzelLand;
		result.nameLand = nameLand;
		return result;

	}

	public NeueSchulePayload() {

		super();

	}

	public String name() {

		return name;
	}

	public String kuerzelOrt() {

		return kuerzelOrt;
	}

	public String nameOrt() {

		return nameOrt;
	}

	public String kuerzelLand() {

		return kuerzelLand;
	}

	public String nameLand() {

		return nameLand;
	}

	public String kuerzel() {

		return kuerzel;
	}
}
