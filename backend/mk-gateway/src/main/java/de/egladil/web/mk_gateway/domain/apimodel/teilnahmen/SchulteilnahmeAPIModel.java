// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.teilnahmen;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.AuswertungsgruppeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;

/**
 * SchulteilnahmeAPIModel
 */
public class SchulteilnahmeAPIModel {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private Teilnahmeart teilnahmeart;

	@JsonProperty
	private String nameUrkunde;

	@JsonProperty
	private int anzahlKlassen;

	@JsonProperty
	private int anzahlKinder = 0;

	@JsonProperty
	private boolean klassenGeladen = false;

	@JsonProperty
	private List<AuswertungsgruppeAPIModel> auswertungsgruppen = new ArrayList<>();

	public static SchulteilnahmeAPIModel create(final Schulteilnahme teilnahme) {

		SchulteilnahmeAPIModel result = new SchulteilnahmeAPIModel();
		result.jahr = teilnahme.wettbewerbID().jahr().intValue();
		result.teilnahmenummer = teilnahme.teilnahmenummer().identifier();
		result.teilnahmeart = Teilnahmeart.SCHULE;
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

	public int getJahr() {

		return jahr;
	}

	public String teilnahmenummer() {

		return teilnahmenummer;
	}

	public Teilnahmeart teilnahmeart() {

		return teilnahmeart;
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

}
