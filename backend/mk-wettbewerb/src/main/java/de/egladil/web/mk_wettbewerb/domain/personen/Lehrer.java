// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;

/**
 * Lehrer
 */
public class Lehrer extends Veranstalter {

	@JsonProperty
	private List<Identifier> schulen;

	/**
	 * @param person
	 * @param schulen
	 */
	public Lehrer(final Person person, final boolean newsletterEmpfaenger, final List<Identifier> schulen) {

		super(person, newsletterEmpfaenger);
		this.schulen = schulen;

	}

	@Override
	public Rolle rolle() {

		return Rolle.LEHRER;
	}

	@Override
	public Teilnahmeart teilnahmeart() {

		return Teilnahmeart.SCHULE;
	}

	/**
	 * @return List unmodifiable
	 */
	public List<Identifier> schulen() {

		return Collections.unmodifiableList(schulen);
	}

	@Override
	public List<Identifier> teilnahmeIdentifier() {

		return this.schulen();
	}

	@Override
	public String toString() {

		return uuid() + " - " + fullName() + " (LEHRER)";
	}
}
