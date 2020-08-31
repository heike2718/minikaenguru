// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;

/**
 * Privatperson
 */
public class Privatperson extends Veranstalter {

	// wenn sich eine Privatperson registriert, wird genau eine Teilnahmenummer generiert.
	@JsonProperty
	private List<Identifier> teilnahmenummern = new ArrayList<>();

	Privatperson() {

		super();

	}

	/**
	 * @param person
	 * @param teilnahmekuerzel
	 */
	public Privatperson(final Person person, final boolean newsletterEmpfaenger, final List<Identifier> teilnahmenummern) {

		super(person, newsletterEmpfaenger);

		if (teilnahmenummern == null) {

			throw new IllegalArgumentException("teilnahmenummern darf nicht null sein.");
		}

		this.teilnahmenummern = teilnahmenummern;

	}

	@Override
	public Rolle rolle() {

		return Rolle.PRIVAT;
	}

	@Override
	public Teilnahmeart teilnahmeart() {

		return Teilnahmeart.PRIVAT;
	}

	@Override
	public List<Identifier> teilnahmeIdentifier() {

		return Collections.unmodifiableList(this.teilnahmenummern);

	}

	@Override
	public String toString() {

		return uuid() + " - " + fullName() + " (PRIVAT)";
	}
}
