// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.personen;

import java.util.Collections;
import java.util.List;

import de.egladil.web.mk_wettbewerb.domain.model.Identifier;

/**
 * Lehrer
 */
public class Lehrer extends Veranstalter {

	/**
	 * @param person
	 */
	public Lehrer(final Person person) {

		super(person);

	}

	/**
	 * @param person
	 * @param teilnahmekuerzel
	 */
	public Lehrer(final Person person, final List<Identifier> teilnahmekuerzel) {

		super(person, teilnahmekuerzel);

	}

	@Override
	public Rolle rolle() {

		return Rolle.LEHRER;
	}

	/**
	 * @return List unmodifiable
	 */
	public List<Identifier> schulen() {

		return Collections.unmodifiableList(this.teilnahmekuerzel());
	}

	@Override
	public String toString() {

		return fullName() + " (LEHRER)";
	}
}
