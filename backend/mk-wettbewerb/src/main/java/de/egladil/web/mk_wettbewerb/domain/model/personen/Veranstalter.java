// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.personen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.model.Identifier;

/**
 * Veranstalter
 */
public abstract class Veranstalter {

	@JsonProperty
	private final Person person;

	@JsonProperty
	private final List<Identifier> teilnahmekuerzel;

	/**
	 * @param person
	 */
	public Veranstalter(final Person person) {

		this.person = person;
		this.teilnahmekuerzel = new ArrayList<>();
	}

	/**
	 * @param person
	 * @param teilnahmekuerzel
	 */
	public Veranstalter(final Person person, final List<Identifier> teilnahmekuerzel) {

		this.person = person;
		this.teilnahmekuerzel = teilnahmekuerzel;
	}

	public abstract Rolle rolle();

	public String uuid() {

		return this.person.uuid();
	}

	public Person person() {

		return person;
	}

	public String fullName() {

		return this.person.fullName();
	}

	protected List<Identifier> teilnahmekuerzel() {

		return teilnahmekuerzel;
	}

	/**
	 * Alle Teilnahmekuerzel als kommaseparierter String.
	 *
	 * @return
	 */
	public String persistierbareTeilnahmekuerzel() {

		if (this.teilnahmekuerzel.isEmpty()) {

			return null;
		}
		List<String> kuerzel = teilnahmekuerzel.stream().map(k -> k.identifier()).collect(Collectors.toList());

		return StringUtils.join(kuerzel.toArray(new String[0]), ",");

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
		Veranstalter other = (Veranstalter) obj;
		return Objects.equals(person, other.person);
	}

}
