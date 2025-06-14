// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Schule;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * SchulePayload
 */
public class SchulePayload {

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

	public static SchulePayload create(final Schule schule) {

		SchulePayload result = new SchulePayload();
		result.kuerzel = schule.getKuerzel();
		result.name = schule.getName();
		result.kuerzelOrt = schule.getOrtKuerzel();
		result.nameOrt = schule.getOrtName();
		result.kuerzelLand = schule.getLandKuerzel();
		result.nameLand = schule.getLandName();

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

	@Override
	public String toString() {

		return "SchulePayload [kuerzel=" + kuerzel + ", name=" + name + ", kuerzelOrt=" + kuerzelOrt + ", nameOrt=" + nameOrt
			+ ", kuerzelLand=" + kuerzelLand + ", nameLand=" + nameLand + "]";
	}

	public String kuerzel() {

		return kuerzel;
	}

	public SchulePayload withKuerzel(String kuerzel) {
		this.kuerzel = kuerzel;
		return this;
	}

	public SchulePayload withName(String name) {
		this.name = name;
		return this;
	}

	public SchulePayload withKuerzelOrt(String kuerzelOrt) {
		this.kuerzelOrt = kuerzelOrt;
		return this;
	}

	public SchulePayload withNameOrt(String nameOrt) {
		this.nameOrt = nameOrt;
		return this;
	}

	public SchulePayload withKuerzelLand(String kuerzelLand) {
		this.kuerzelLand = kuerzelLand;
		return this;
	}

	public SchulePayload withNameLand(String nameLand) {
		this.nameLand = nameLand;
		return this;
	}
}
