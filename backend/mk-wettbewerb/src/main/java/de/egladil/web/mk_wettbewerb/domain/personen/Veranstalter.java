// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.semantik.Aggregate;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.ZugangsberechtigungUnterlagen;

/**
 * Veranstalter
 */
@Aggregate
public abstract class Veranstalter {

	@JsonProperty
	private Person person;

	@JsonProperty
	private List<Identifier> teilnahmekuerzel;

	@JsonProperty
	private ZugangsberechtigungUnterlagen zugangsberechtigungUnterlagen = ZugangsberechtigungUnterlagen.DEFAULT;

	/**
	 * @param person
	 */
	public Veranstalter(final Person person) {

		if (person == null) {

			throw new IllegalArgumentException("person darf nicht null sein.");
		}

		this.person = person;
		this.teilnahmekuerzel = new ArrayList<>();
	}

	/**
	 * @param person
	 * @param teilnahmekuerzel
	 */
	public Veranstalter(final Person person, final List<Identifier> teilnahmekuerzel) {

		this(person);

		if (teilnahmekuerzel == null) {

			throw new IllegalArgumentException("teilnahmekuerzel darf nicht null sein.");

		}

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

	public ZugangsberechtigungUnterlagen zugangsberechtigungUnterlagen() {

		return zugangsberechtigungUnterlagen;
	}

	public void erlaubeZugangUnterlagen() {

		this.zugangsberechtigungUnterlagen = ZugangsberechtigungUnterlagen.ERTEILT;

	}

	public void verwehreZugangUnterlagen() {

		this.zugangsberechtigungUnterlagen = ZugangsberechtigungUnterlagen.ENTZOGEN;

	}

	public void setzeZugangUnterlagenZurueck() {

		this.zugangsberechtigungUnterlagen = ZugangsberechtigungUnterlagen.DEFAULT;
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
