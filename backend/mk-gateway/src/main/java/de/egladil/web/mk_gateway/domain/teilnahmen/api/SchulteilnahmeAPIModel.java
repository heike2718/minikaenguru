// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.api;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.AuswertungsgruppeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;

/**
 * SchulteilnahmeAPIModel
 */
public class SchulteilnahmeAPIModel {

	@JsonProperty
	private TeilnahmeIdentifier identifier;

	@JsonProperty
	private String nameUrkunde;

	@JsonProperty
	private int anzahlKlassen;

	@JsonProperty
	private int anzahlKinder = 0;

	@JsonProperty
	private boolean klassenGeladen = false;

	@JsonProperty
	private String angemeldetDurch;

	@JsonProperty
	private List<AuswertungsgruppeAPIModel> auswertungsgruppen = new ArrayList<>();

	public static SchulteilnahmeAPIModel create(final Schulteilnahme teilnahme) {

		SchulteilnahmeAPIModel result = new SchulteilnahmeAPIModel();
		result.identifier = TeilnahmeIdentifier.createFromTeilnahme(teilnahme);
		result.nameUrkunde = teilnahme.nameSchule();

		return result;
	}

	public SchulteilnahmeAPIModel withKlassenGeladen(final boolean klassenGeladen) {

		this.klassenGeladen = klassenGeladen;
		return this;
	}

	public SchulteilnahmeAPIModel withAnzahlKinder(final int anzahlKinder) {

		this.anzahlKinder = anzahlKinder;
		return this;

	}

	public SchulteilnahmeAPIModel withAnzahlKlassen(final int anzahlKlassen) {

		this.anzahlKlassen = anzahlKlassen;
		return this;

	}

	public SchulteilnahmeAPIModel withAuswertungsgruppen(final List<AuswertungsgruppeAPIModel> auswertungsgruppen) {

		this.auswertungsgruppen = auswertungsgruppen;
		this.klassenGeladen = true;
		return this;

	}

	public TeilnahmeIdentifier identifier() {

		return this.identifier;
	}

	public String nameUrkunde() {

		return nameUrkunde;
	}

	public int anzahlKlassen() {

		return anzahlKlassen;
	}

	public int anzahlKinder() {

		return anzahlKinder;
	}

	public boolean klassenGeladen() {

		return klassenGeladen;
	}

	public List<AuswertungsgruppeAPIModel> getAuswertungsgruppen() {

		return auswertungsgruppen;
	}

	public String angemeldetDurch() {

		return angemeldetDurch;
	}

	public SchulteilnahmeAPIModel withAngemeldetDurch(final String angemeldetDurch) {

		this.angemeldetDurch = angemeldetDurch;
		return this;
	}

}
