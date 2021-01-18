// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.api;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AnmeldungsitemAPIModel
 */
public class AnmeldungsitemAPIModel {

	@JsonProperty
	private String name;

	@JsonProperty
	private int anzahlAnmeldungen;

	@JsonProperty
	private int anzahlLoesungszettel;

	@Override
	public int hashCode() {

		return Objects.hash(name);
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
		AnmeldungsitemAPIModel other = (AnmeldungsitemAPIModel) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public String toString() {

		return name + ": [anzahlAnmeldungen=" + anzahlAnmeldungen + ", anzahlLoesungszettel="
			+ anzahlLoesungszettel + "]";
	}

	public String getName() {

		return name;
	}

	public AnmeldungsitemAPIModel withName(final String name) {

		this.name = name;
		return this;
	}

	public int getAnzahlAnmeldungen() {

		return anzahlAnmeldungen;
	}

	public AnmeldungsitemAPIModel withAnzahlAnmeldungen(final int anzahlAnmeldungen) {

		this.anzahlAnmeldungen = anzahlAnmeldungen;
		return this;
	}

	public int getAnzahlLoesungszettel() {

		return anzahlLoesungszettel;
	}

	public AnmeldungsitemAPIModel withAnzahlLoesungszettel(final int anzahlLoesungszettel) {

		this.anzahlLoesungszettel = anzahlLoesungszettel;
		return this;
	}

}
