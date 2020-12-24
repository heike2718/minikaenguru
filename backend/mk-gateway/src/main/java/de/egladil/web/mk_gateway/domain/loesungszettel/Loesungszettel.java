// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * Loesungszettel
 */
@AggregateRoot
public class Loesungszettel {

	private Identifier identifier;

	private Identifier kindID;

	private Klassenstufe klassenstufe;

	private Sprache sprache;

	private TeilnahmeIdentifier teilnahmeIdentifier;

	private String landkuerzel;

	private int laengeKaengurusprung;

	private int punkte;

	private Auswertungsquelle auswertungsquelle;

	private LoesungszettelRohdaten rohdaten;

	public Loesungszettel() {

	}

	public Loesungszettel(final Identifier identifier) {

		this.identifier = identifier;
	}

	public Identifier identifier() {

		return identifier;
	}

	public Identifier kindID() {

		return kindID;
	}

	public Loesungszettel withIdentifier(final Identifier identifier) {

		if (this.identifier != null) {

			throw new IllegalStateException("Der identifier darf nicht geändert werden!");
		}
		this.identifier = identifier;
		return this;
	}

	public Loesungszettel withKindID(final Identifier kindID) {

		this.kindID = kindID;
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

	@Override
	public String toString() {

		return "Loesungszettel [identifier=" + identifier + ", klassenstufe=" + klassenstufe + ", teilnahmeIdentifier="
			+ teilnahmeIdentifier + ", punkte=" + this.punkte + "]";
	}

	public Identifier getTheTeilnahmenummer() {

		return new Identifier(this.teilnahmeIdentifier.teilnahmenummer());
	}
}
