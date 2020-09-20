// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;

/**
 * LoesungszettelRohdaten
 */
@ValueObject
public class LoesungszettelRohdaten {

	@JsonProperty
	private boolean typo;

	@JsonProperty
	private String nutzereingabe;

	@JsonProperty
	private String antwortcode;

	@JsonProperty
	private String wertungscode;

	public LoesungszettelRohdaten() {

	}

	public boolean hatTypo() {

		return typo;
	}

	public LoesungszettelRohdaten withTypo(final boolean typo) {

		this.typo = typo;
		return this;
	}

	public String nutzereingabe() {

		return nutzereingabe;
	}

	public LoesungszettelRohdaten withNutzereingabe(final String nutzereingabe) {

		this.nutzereingabe = nutzereingabe;
		return this;
	}

	public String antwortcode() {

		return antwortcode;
	}

	public LoesungszettelRohdaten withAntwortcode(final String antwortcode) {

		this.antwortcode = antwortcode;
		return this;
	}

	public String wertungscode() {

		return wertungscode;

	}

	public LoesungszettelRohdaten withWertungscode(final String wertungscode) {

		this.wertungscode = wertungscode;
		return this;
	}

}
