// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.kinder.Klasse;

/**
 * KlasseAPIModel
 */
public class KlasseAPIModel implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = -5048069037139843806L;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String name;

	@JsonProperty
	private String schulkuerzel;

	@JsonProperty
	private long anzahlKinder;

	@JsonProperty
	private long anzahlLoesungszettel;

	public static KlasseAPIModel createFromKlasse(final Klasse klasse) {

		KlasseAPIModel result = new KlasseAPIModel(klasse.identifier().identifier());
		result.name = klasse.name();
		result.schulkuerzel = klasse.schuleID().identifier();
		return result;
	}

	public KlasseAPIModel() {

	}

	public KlasseAPIModel(final String uuid) {

		this.uuid = uuid;
	}

	public String name() {

		return name;
	}

	public KlasseAPIModel withName(final String name) {

		this.name = name;
		return this;
	}

	public String uuid() {

		return uuid;
	}

	public String schulkuerzel() {

		return schulkuerzel;
	}

	public KlasseAPIModel withSchulkuerzel(final String schulkuerzel) {

		this.schulkuerzel = schulkuerzel;
		return this;
	}

	public long anzahlKinder() {

		return anzahlKinder;
	}

	public KlasseAPIModel withAnzahlKinder(final long anzahlKinder) {

		this.anzahlKinder = anzahlKinder;
		return this;
	}

	public long anzahlLoesungszettel() {

		return anzahlLoesungszettel;
	}

	public KlasseAPIModel withAnzahlLoesungszettel(final long anzahlLoesungszettel) {

		this.anzahlLoesungszettel = anzahlLoesungszettel;
		return this;
	}
}
