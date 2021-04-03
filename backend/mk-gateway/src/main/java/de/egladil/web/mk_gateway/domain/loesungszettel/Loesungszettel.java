// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.statistik.functions.PunkteStringMapper;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * Loesungszettel. Ist JSON-annotiert für die Datenbanklosen Tests.
 */
@AggregateRoot
public class Loesungszettel {

	@JsonProperty
	private Identifier identifier;

	@JsonProperty
	private Identifier kindID;

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

	@JsonIgnore
	private int version;

	public Loesungszettel() {

	}

	public Loesungszettel(final Identifier identifier) {

		this.identifier = identifier;
	}

	@Override
	public int hashCode() {

		return Objects.hash(identifier);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		Loesungszettel other = (Loesungszettel) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {

		return "Loesungszettel [identifier=" + identifier + ", kindID=" + kindID + ", klassenstufe=" + klassenstufe + ", sprache="
			+ sprache + ", teilnahmeIdentifier=" + teilnahmeIdentifier + ", punkte=" + punkte + ", laengeKaengurusprung="
			+ laengeKaengurusprung + "]";
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

	/**
	 * Gibt die Punkte als String mit 2 Dezimalstellen und einem Komma zurück, so wie es in der UIs / PDFs angezeigt wird.
	 *
	 * @return
	 */
	public String punkteAsString() {

		return new PunkteStringMapper().apply(this.punkte);
	}

	public Identifier getTheTeilnahmenummer() {

		return new Identifier(this.teilnahmeIdentifier.teilnahmenummer());
	}

	public int version() {

		return version;
	}

	public Loesungszettel withVersion(final int version) {

		this.version = version;
		return this;
	}
}
