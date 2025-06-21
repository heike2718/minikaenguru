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
	private long anzahlKinder;

	@JsonProperty
	private long anzahlLoesungszettelOnline;

	@JsonProperty
	private long anzahlLoesungszettelUpload;

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

	public long anzahlKinder() {

		return anzahlKinder;
	}

	public AnonymisierteTeilnahmeAPIModel withAnzahlKinder(final long anzahlKinder) {

		this.anzahlKinder = anzahlKinder;
		return this;
	}

	public long getAnzahlLoesungszettelOnline() {

		return anzahlLoesungszettelOnline;
	}

	public AnonymisierteTeilnahmeAPIModel withAnzahlLoesungszettelOnline(final long anzahlLoesungszettelOnline) {

		this.anzahlLoesungszettelOnline = anzahlLoesungszettelOnline;
		return this;
	}

	public long getAnzahlLoesungszettelUpload() {

		return anzahlLoesungszettelUpload;
	}

	public AnonymisierteTeilnahmeAPIModel withAnzahlLoesungszettelUpload(final long anzahlLoesungszettelUpload) {

		this.anzahlLoesungszettelUpload = anzahlLoesungszettelUpload;
		return this;
	}
}
