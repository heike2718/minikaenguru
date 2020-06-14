// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.semantik.Aggregate;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;

/**
 * Veranstalter
 */
@Aggregate
public abstract class Veranstalter {

	@JsonProperty
	private Person person;

	@JsonProperty
	private ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.DEFAULT;

	/**
	 * @param person
	 */
	public Veranstalter(final Person person) {

		if (person == null) {

			throw new IllegalArgumentException("person darf nicht null sein.");
		}

		this.person = person;

	}

	/**
	 * @return Rolle
	 */
	public abstract Rolle rolle();

	/**
	 * @return Teilnahmeart
	 */
	public abstract Teilnahmeart teilnahmeart();

	public String uuid() {

		return this.person.uuid();
	}

	public Person person() {

		return person;
	}

	public String fullName() {

		return this.person.fullName();
	}

	/**
	 * Gibt die Tielnahme-Identifier zurück, die dieser Veranstalter hat. Bei einem Lehrer sind es die Schulkürzel, bei einer
	 * Privatperson ein Kürzel, welches beim Registrieren einer Privatperson angelegt wird.
	 *
	 * @return List<Identifier>
	 */
	public abstract List<Identifier> teilnahmeIdentifier();

	public ZugangUnterlagen zugangUnterlagen() {

		return zugangUnterlagen;
	}

	public void erlaubeZugangUnterlagen() {

		this.zugangUnterlagen = ZugangUnterlagen.ERTEILT;

	}

	public void verwehreZugangUnterlagen() {

		this.zugangUnterlagen = ZugangUnterlagen.ENTZOGEN;

	}

	public void setzeZugangUnterlagenZurueck() {

		this.zugangUnterlagen = ZugangUnterlagen.DEFAULT;
	}

	/**
	 * Alle Teilnahmekuerzel als kommaseparierter String.
	 *
	 * @return String
	 */
	public String persistierbareTeilnahmenummern() {

		if (this.teilnahmeIdentifier().isEmpty()) {

			return null;
		}
		List<String> kuerzel = teilnahmeIdentifier().stream().map(k -> k.identifier()).collect(Collectors.toList());

		return StringUtils.join(kuerzel, ",");

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
