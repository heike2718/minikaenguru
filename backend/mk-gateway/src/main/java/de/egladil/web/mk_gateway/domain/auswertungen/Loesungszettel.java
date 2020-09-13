// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.semantik.Aggregate;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * Loesungszettel
 */
@Aggregate
public class Loesungszettel {

	@JsonProperty
	private Identifier identifier;

	@JsonProperty
	private int nummer;

	@JsonProperty
	private Klassenstufe klassenstufe;

	@JsonProperty
	private Sprache sprache;

	@JsonProperty
	private TeilnahmeIdentifier teilnahmeIdentifier;

	@JsonProperty
	private String landkuerzel;

	@JsonProperty
	private int laengeKaengurusprung;

	@JsonProperty
	private int punkte;

	@JsonProperty
	private Auswertungsquelle auswertungsquelle;

	@JsonProperty
	private LoesungszettelRohdaten rohdaten;

	public Loesungszettel() {

	}

	public Loesungszettel(final Identifier identifier) {

		this.identifier = identifier;
	}

	public Identifier identifier() {

		return identifier;
	}

	public int nummer() {

		return nummer;
	}

	public Loesungszettel withNummer(final int nummer) {

		this.nummer = nummer;
		return this;
	}

	public Klassenstufe klassenstufe() {

		return klassenstufe;
	}

	public Loesungszettel withKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public Sprache sprache() {

		return sprache;
	}

	public Loesungszettel withSprache(final Sprache sprache) {

		this.sprache = sprache;
		return this;
	}

	public TeilnahmeIdentifier teilnahmeIdentifier() {

		return teilnahmeIdentifier;
	}

	public Loesungszettel withTeilnahmeIdentifier(final TeilnahmeIdentifier teilnahmeIdentifier) {

		this.teilnahmeIdentifier = teilnahmeIdentifier;
		return this;
	}

	public String landkuerzel() {

		return landkuerzel;
	}

	public Loesungszettel withLandkuerzel(final String landkuerzel) {

		this.landkuerzel = landkuerzel;
		return this;
	}

	public int laengeKaengurusprung() {

		return laengeKaengurusprung;
	}

	public Loesungszettel withLaengeKaengurusprung(final int laengeKaengurusprung) {

		this.laengeKaengurusprung = laengeKaengurusprung;
		return this;
	}

	public int punkte() {

		return punkte;
	}

	public Loesungszettel withPunkte(final int punkte) {

		this.punkte = punkte;
		return this;
	}

	public Auswertungsquelle auswertungsquelle() {

		return auswertungsquelle;
	}

	public Loesungszettel withAuswertungsquelle(final Auswertungsquelle auswertungsquelle) {

		this.auswertungsquelle = auswertungsquelle;
		return this;
	}

	public LoesungszettelRohdaten rohdaten() {

		return rohdaten;
	}

	public Loesungszettel withRohdaten(final LoesungszettelRohdaten rohdaten) {

		this.rohdaten = rohdaten;
		return this;
	}

}
