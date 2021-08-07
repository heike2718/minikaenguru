// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AnonymisierteTeilnahmeAPIModel
 */
public class AnonymisierteTeilnahmeAPIModel {

	@JsonProperty
	private TeilnahmeIdentifier identifier;

	@JsonProperty
	private int anzahlKinder;

	@JsonProperty
	private int anzahlLoesungszettelOnline;

	@JsonProperty
	private int anzahlLoesungszettelUpload;

	AnonymisierteTeilnahmeAPIModel() {

	}

	public static AnonymisierteTeilnahmeAPIModel create(final TeilnahmeIdentifier identifier) {

		AnonymisierteTeilnahmeAPIModel result = new AnonymisierteTeilnahmeAPIModel();
		result.identifier = identifier;
		return result;
	}

	public TeilnahmeIdentifier identifier() {

		return identifier;
	}

	public int anzahlKinder() {

		return anzahlKinder;
	}

	public AnonymisierteTeilnahmeAPIModel withAnzahlKinder(final int anzahlKinder) {

		this.anzahlKinder = anzahlKinder;
		return this;
	}

	public int getAnzahlLoesungszettelOnline() {

		return anzahlLoesungszettelOnline;
	}

	public AnonymisierteTeilnahmeAPIModel withAnzahlLoesungszettelOnline(final int anzahlLoesungszettelOnline) {

		this.anzahlLoesungszettelOnline = anzahlLoesungszettelOnline;
		return this;
	}

	public int getAnzahlLoesungszettelUpload() {

		return anzahlLoesungszettelUpload;
	}

	public AnonymisierteTeilnahmeAPIModel withAnzahlLoesungszettelUpload(final int anzahlLoesungszettelUpload) {

		this.anzahlLoesungszettelUpload = anzahlLoesungszettelUpload;
		return this;
	}
}
