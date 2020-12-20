// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;

/**
 * LoesungszettelRohdaten
 */
@ValueObject
public class LoesungszettelRohdaten {

	@JsonIgnore
	private boolean typo;

	/**
	 * Nutzereingabe ist ein String der Länge 6, 12 oder 15 aus den Zeichen ABCDEN (bei Onlineauswertung) bzw. rfn beim Upload.
	 */
	@JsonProperty
	private String nutzereingabe;

	@JsonIgnore
	private String antwortcode;

	@JsonIgnore
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
