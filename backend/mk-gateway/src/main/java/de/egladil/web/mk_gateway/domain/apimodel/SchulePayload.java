// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;

/**
 * SchulePayload
 */
public class SchulePayload {

	@JsonProperty
	@StringLatin
	@NotBlank
	private String name;

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

	@JsonProperty
	@Email
	private String emailAuftraggeber;

	public static SchulePayload create(final String name, final String kuerzelOrt, final String nameOrt, final String kuerzelLand, final String nameLand) {

		SchulePayload result = new SchulePayload();
		result.name = name;
		result.kuerzelOrt = kuerzelOrt;
		result.nameOrt = nameOrt;
		result.kuerzelLand = kuerzelLand;
		result.nameLand = nameLand;
		return result;

	}

	public SchulePayload() {

		super();

	}

	public SchulePayload withEmailAuftraggeber(final String email) {

		this.emailAuftraggeber = email;
		return this;
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

	public String emailAuftraggeber() {

		return emailAuftraggeber;
	}
}
