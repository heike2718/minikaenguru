// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.user.Rolle;

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

	/**
	 * Fügt die gegebene Schule hinzu.
	 *
	 * @param  schuleID
	 * @return
	 */
	public boolean addSchule(final Identifier schuleID) {

		return this.schulen.add(schuleID);
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

	@Override
	public Veranstalter merge(final Person person) {

		List<Identifier> kopieTeilnahmenummern = this.schulen.stream().map(i -> new Identifier(i.identifier()))
			.collect(Collectors.toList());
		return new Lehrer(person, this.isNewsletterEmpfaenger(), kopieTeilnahmenummern);
	}

	public String joinedSchulen() {

		String schulkuerzel = StringUtils.join(schulen.stream().map(i -> i.identifier()).collect(Collectors.toList()),
			",");
		return schulkuerzel;
	}
}
