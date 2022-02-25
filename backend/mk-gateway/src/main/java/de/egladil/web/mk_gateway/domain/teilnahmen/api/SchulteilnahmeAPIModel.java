// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;

/**
 * SchulteilnahmeAPIModel
 */
public class SchulteilnahmeAPIModel {

	@JsonProperty
	private String nameUrkunde;

	@JsonProperty
	private String angemeldetDurch;

	@JsonProperty
	private long anzahlLoesungszettelOnline;

	@JsonProperty
	private long anzahlLoesungszettelUpload;

	public static SchulteilnahmeAPIModel create(final Schulteilnahme teilnahme) {

		SchulteilnahmeAPIModel result = new SchulteilnahmeAPIModel();
		result.nameUrkunde = teilnahme.nameSchule();
		return result;
	}

	public String nameUrkunde() {

		return nameUrkunde;
	}

	public String angemeldetDurch() {

		return angemeldetDurch;
	}

	public SchulteilnahmeAPIModel withAngemeldetDurch(final String angemeldetDurch) {

		this.angemeldetDurch = angemeldetDurch;
		return this;
	}

	public long getAnzahlLoesungszettelOnline() {

		return anzahlLoesungszettelOnline;
	}

	public SchulteilnahmeAPIModel withAnzahlLoesungszettelOnline(final long anzahlLoesungszettelOnline) {

		this.anzahlLoesungszettelOnline = anzahlLoesungszettelOnline;
		return this;
	}

	public long getAnzahlLoesungszettelUpload() {

		return anzahlLoesungszettelUpload;
	}

	public SchulteilnahmeAPIModel withAnzahlLoesungszettelUpload(final long anzahlLoesungszettelUpload) {

		this.anzahlLoesungszettelUpload = anzahlLoesungszettelUpload;
		return this;
	}

}
