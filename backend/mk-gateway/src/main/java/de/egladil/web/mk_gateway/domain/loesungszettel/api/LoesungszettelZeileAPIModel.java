// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.loesungszettel.ZulaessigeLoesungszetteleingabe;

/**
 * LoesungszettelZeileAPIModel
 */
public class LoesungszettelZeileAPIModel implements Comparable<LoesungszettelZeileAPIModel> {

	@JsonProperty
	private int index;

	@JsonProperty
	private int anzahlSpalten;

	@JsonProperty // A-1,..., C-5
	private String name;

	@JsonProperty
	private ZulaessigeLoesungszetteleingabe eingabe;

	@Override
	public int compareTo(final LoesungszettelZeileAPIModel o) {

		return this.index - o.index;
	}

	public int index() {

		return index;
	}

	public LoesungszettelZeileAPIModel withIndex(final int index) {

		this.index = index;
		return this;
	}

	public int anzahlSpalten() {

		return anzahlSpalten;
	}

	public LoesungszettelZeileAPIModel withAnzahlSpalten(final int anzahlSpalten) {

		this.anzahlSpalten = anzahlSpalten;
		return this;
	}

	public String name() {

		return name;
	}

	public LoesungszettelZeileAPIModel withName(final String name) {

		this.name = name;
		return this;
	}

	public ZulaessigeLoesungszetteleingabe eingabe() {

		return eingabe;
	}

	public LoesungszettelZeileAPIModel withEingabe(final ZulaessigeLoesungszetteleingabe eingabe) {

		this.eingabe = eingabe;
		return this;
	}

}
