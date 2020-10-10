// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * AktuelleSchulteilnahmeData
 */
public class AktuelleSchulteilnahmeData {

	@JsonProperty
	private TeilnahmeIdentifier identifier;

	@JsonProperty
	private String nameUrkunde;

	@JsonProperty
	private String angemeldetDurch;

	@JsonProperty
	private int anzahlKlassen;

	@JsonProperty
	private int anzahlKinder = 0;

	public static AktuelleSchulteilnahmeData createFromSchulteilnahmeAPIModel(final SchulteilnahmeAPIModel schulteilnahme) {

		AktuelleSchulteilnahmeData result = new AktuelleSchulteilnahmeData();
		result.anzahlKinder = schulteilnahme.anzahlKinder();
		result.anzahlKlassen = schulteilnahme.anzahlKlassen();
		result.identifier = schulteilnahme.identifier();
		result.nameUrkunde = schulteilnahme.nameUrkunde();
		result.angemeldetDurch = schulteilnahme.angemeldetDurch();
		return result;
	}

	public TeilnahmeIdentifier identifier() {

		return identifier;
	}

	public AktuelleSchulteilnahmeData withIdentifier(final TeilnahmeIdentifier identifier) {

		this.identifier = identifier;
		return this;
	}

	public String nameUrkunde() {

		return nameUrkunde;
	}

	public AktuelleSchulteilnahmeData withNameUrkunde(final String nameUrkunde) {

		this.nameUrkunde = nameUrkunde;
		return this;
	}

	public int anzahlKlassen() {

		return anzahlKlassen;
	}

	public AktuelleSchulteilnahmeData withAnzahlKlassen(final int anzahlKlassen) {

		this.anzahlKlassen = anzahlKlassen;
		return this;
	}

	public int anzahlKinder() {

		return anzahlKinder;
	}

	public AktuelleSchulteilnahmeData withAnzahlKinder(final int anzahlKinder) {

		this.anzahlKinder = anzahlKinder;
		return this;
	}

	public String angemeldetDurch() {

		return angemeldetDurch;
	}

	public AktuelleSchulteilnahmeData withAngemeldetDurch(final String angemeldetDurch) {

		this.angemeldetDurch = angemeldetDurch;
		return this;
	}

}
