// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.egladil.web.mk_wettbewerb.domain.Identifier;

/**
 * Privatperson
 */
public class Privatperson extends Veranstalter {

	// wenn sich eine Privatperson registriert, wird noch keine Teilnahme angelegt. Wenn sie sich zu einem Wettbewerb anmeldet, wird
	// eine Teilnahme angelegt und das Teilnahmekuerzel der Person zugeordnet.
	// Hat die Person ein Teilnahmekürzel und meldet sich zu einem anderen Wettbewerb an, wird mit diesem Teilnahmekuerzel eine
	// Teilnahme angelegt.
	private final List<Identifier> teilnahmenummern;

	/**
	 * @param person
	 */
	public Privatperson(final Person person) {

		super(person);
		this.teilnahmenummern = new ArrayList<>();

	}

	/**
	 * @param person
	 * @param teilnahmekuerzel
	 */
	public Privatperson(final Person person, final List<Identifier> teilnahmenummern) {

		super(person);

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
	protected List<Identifier> teilnahmeIdentifier() {

		return Collections.unmodifiableList(this.teilnahmenummern);

	}

	@Override
	public String toString() {

		return fullName() + " (PRIVAT)";
	}
}
