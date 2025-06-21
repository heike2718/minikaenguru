// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * Privatveranstalter
 */
public class Privatveranstalter extends Veranstalter {

	// wenn sich eine Privatveranstalter registriert, wird genau eine Teilnahmenummer generiert.
	@JsonProperty
	private List<Identifier> teilnahmenummern = new ArrayList<>();

	Privatveranstalter() {

		super();

	}

	/**
	 * @param person
	 * @param teilnahmekuerzel
	 */
	public Privatveranstalter(final Person person, final boolean newsletterEmpfaenger, final List<Identifier> teilnahmenummern) {

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

	@Override
	public Veranstalter merge(final Person person) {

		List<Identifier> kopieTeilnahmenummern = this.teilnahmenummern.stream().map(i -> new Identifier(i.identifier()))
			.collect(Collectors.toList());
		return new Privatveranstalter(person, this.isNewsletterEmpfaenger(), kopieTeilnahmenummern);
	}
}
