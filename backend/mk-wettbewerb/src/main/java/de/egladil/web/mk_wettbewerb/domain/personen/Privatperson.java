// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.List;

import de.egladil.web.mk_wettbewerb.domain.Identifier;

/**
 * Privatperson
 */
public class Privatperson extends Veranstalter {

	/**
	 * @param person
	 */
	public Privatperson(final Person person) {

		super(person);

	}

	/**
	 * @param person
	 * @param teilnahmekuerzel
	 */
	public Privatperson(final Person person, final List<Identifier> teilnahmekuerzel) {

		super(person, teilnahmekuerzel);

	}

	@Override
	public Rolle rolle() {

		return Rolle.PRIVAT;
	}

	@Override
	public String toString() {

		return fullName() + " (PRIVAT)";
	}
}
