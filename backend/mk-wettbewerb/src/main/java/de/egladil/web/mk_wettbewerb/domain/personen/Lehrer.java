// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.Identifier;

/**
 * Lehrer
 */
public class Lehrer extends Veranstalter {

	@JsonProperty
	private List<Identifier> schulen;

	/**
	 * @param person
	 */
	public Lehrer(final Person person) {

		super(person);

	}

	/**
	 * @param person
	 * @param schulen
	 */
	public Lehrer(final Person person, final List<Identifier> schulen) {

		super(person);
		this.schulen = schulen;

	}

	@Override
	public Rolle rolle() {

		return Rolle.LEHRER;
	}

	/**
	 * @return List unmodifiable
	 */
	public List<Identifier> schulen() {

		return Collections.unmodifiableList(schulen);
	}

	@Override
	protected List<Identifier> teilnahmeIdentifier() {

		return this.schulen();
	}

	@Override
	public String toString() {

		return fullName() + " (LEHRER)";
	}
}
