// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.apimodel;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;
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
	private String emailAuftraggeber = "";

	public static SchulePayload create(final String kuerzel, final String name, final String kuerzelOrt, final String nameOrt, final String kuerzelLand, final String nameLand) {

		SchulePayload result = new SchulePayload();
		result.kuerzel = kuerzel;
		result.name = name;
		result.kuerzelOrt = kuerzelOrt;
		result.nameOrt = nameOrt;
		result.kuerzelLand = kuerzelLand;
		result.nameLand = nameLand;
		return result;

	}

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

	public String kuerzel() {

		return kuerzel;
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
	public int hashCode() {

		return Objects.hash(kuerzel, kuerzelLand, kuerzelOrt, name, nameLand, nameOrt);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		SchulePayload other = (SchulePayload) obj;
		return Objects.equals(kuerzel, other.kuerzel) && Objects.equals(kuerzelLand, other.kuerzelLand)
			&& Objects.equals(kuerzelOrt, other.kuerzelOrt) && Objects.equals(name, other.name)
			&& Objects.equals(nameLand, other.nameLand) && Objects.equals(nameOrt, other.nameOrt);
	}

	@Override
	public String toString() {

		return "SchulePayload [kuerzel=" + kuerzel + ", name=" + name + ", kuerzelOrt=" + kuerzelOrt + ", nameOrt=" + nameOrt
			+ ", kuerzelLand=" + kuerzelLand + ", nameLand=" + nameLand + "]";
	}

}
