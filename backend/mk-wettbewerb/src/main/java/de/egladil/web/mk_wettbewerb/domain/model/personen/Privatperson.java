// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.personen;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Privatperson
 */
public class Privatperson {

	@JsonProperty
	private final Person person;

	/**
	 * @param person
	 */
	public Privatperson(final Person person) {

		if (person == null) {

			throw new IllegalArgumentException("person darf nicht null sein");
		}

		this.person = person;
	}

	public String fullName() {

		return this.person.fullName();
	}

	@Override
	public int hashCode() {

		return Objects.hash(person);
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
		Privatperson other = (Privatperson) obj;
		return Objects.equals(person, other.person);
	}

	@Override
	public String toString() {

		return fullName();
	}
}
