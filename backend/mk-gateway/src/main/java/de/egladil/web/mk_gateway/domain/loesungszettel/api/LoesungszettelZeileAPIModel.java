// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.api;

import java.util.Objects;

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

	@Override
	public String toString() {

		return "LoesungszettelZeileAPIModel [index=" + index + ", anzahlSpalten=" + anzahlSpalten + ", name=" + name + ", eingabe="
			+ eingabe + "]";
	}

	@Override
	public int hashCode() {

		return Objects.hash(anzahlSpalten, eingabe, index, name);
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
		LoesungszettelZeileAPIModel other = (LoesungszettelZeileAPIModel) obj;
		return anzahlSpalten == other.anzahlSpalten && eingabe == other.eingabe && index == other.index
			&& Objects.equals(name, other.name);
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
