// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kollege
 */
public class Kollege {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	public static Kollege fromPerson(final Person person) {

		return new Kollege(person.uuid(), person.fullName());
	}

	Kollege() {

	}

	public Kollege(final String uuid, final String fullName) {

		this.uuid = uuid;
		this.fullName = fullName;
	}

	@Override
	public int hashCode() {

		return Objects.hash(uuid);
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
		Kollege other = (Kollege) obj;
		return Objects.equals(uuid, other.uuid);
	}

	@Override
	public String toString() {

		return "Kollege [uuid=" + uuid + ", fullName=" + fullName + "]";
	}

	public String uuid() {

		return uuid;
	}

	public String fullName() {

		return fullName;
	}
}
