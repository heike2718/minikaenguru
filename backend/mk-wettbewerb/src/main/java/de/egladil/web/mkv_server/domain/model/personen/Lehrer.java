// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.domain.model.personen;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkv_server.domain.model.teilnahme.Schulkuerzel;

/**
 * Lehrer
 */
public class Lehrer {

	@JsonProperty
	private final Person person;

	@JsonProperty
	private final List<Schulkuerzel> schulen;

	/**
	 * @param uuid
	 * @param fullName
	 */
	public Lehrer(final Person person, final List<Schulkuerzel> schulen) {

		if (person == null) {

			throw new IllegalArgumentException("person darf nicht null sein");
		}

		if (schulen == null) {

			throw new IllegalArgumentException("schulen darf nicht null sein");
		}
		this.person = person;
		this.schulen = schulen;
	}

	public String fullName() {

		return this.person.fullName();
	}

	/**
	 * @return List unmodifiable
	 */
	public List<Schulkuerzel> schulen() {

		return Collections.unmodifiableList(this.schulen);
	}

	@Override
	public String toString() {

		return fullName();
	}

}
