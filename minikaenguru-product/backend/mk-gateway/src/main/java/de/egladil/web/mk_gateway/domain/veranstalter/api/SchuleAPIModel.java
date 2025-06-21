// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;

/**
 * SchuleAPIModel
 */
public class SchuleAPIModel {

	@JsonProperty
	@Kuerzel
	private String kuerzel;

	@JsonProperty
	private String name;

	@JsonProperty
	private String ort;

	@JsonProperty
	private String land;

	@JsonProperty
	private String kuerzelLand;

	@JsonProperty
	private boolean aktuellAngemeldet;

	@JsonProperty
	private Auswertungsmodus auswertungsmodus;

	@JsonProperty
	private SchuleDetails details;

	/**
	 * @param  schuleAusKatalog
	 * @param  schuleAusWettbewerbAPI
	 * @return
	 */
	public static SchuleAPIModel merge(final SchuleAPIModel schuleAusKatalog, final SchuleDetails schuleAusWettbewerbAPI) {

		SchuleAPIModel result = new SchuleAPIModel();

		result.kuerzel = schuleAusKatalog.kuerzel;
		result.name = schuleAusKatalog.name;
		result.ort = schuleAusKatalog.ort;
		result.land = schuleAusKatalog.land;
		result.kuerzelLand = schuleAusKatalog.kuerzelLand;

		result.aktuellAngemeldet = schuleAusWettbewerbAPI.angemeldetDurch() != null;
		result.details = schuleAusWettbewerbAPI;
		return result;
	}



	public SchuleAPIModel markKatalogeintragUnknown() {

		name = "unbekannter Schulname";
		ort = "unbekannter Ort";
		land = "unbekanntes Land / Bundesland";
		return this;
	}

	public String kuerzel() {

		return kuerzel;
	}

	public boolean aktuellAngemeldet() {

		return aktuellAngemeldet;
	}

	public String name() {

		return name;
	}

	public String ort() {

		return ort;
	}

	public String land() {

		return land;
	}

	public String kuerzelLand() {

		return kuerzelLand;
	}

	public SchuleDetails details() {

		return details;
	}

	@Override
	public int hashCode() {

		return Objects.hash(kuerzel);
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
		SchuleAPIModel other = (SchuleAPIModel) obj;
		return Objects.equals(kuerzel, other.kuerzel);
	}

	@Override
	public String toString() {

		return "SchuleAPIModel [kuerzel=" + kuerzel + "]";
	}

	public Auswertungsmodus getAuswertungsmodus() {

		return auswertungsmodus;
	}



	public SchuleAPIModel withKuerzel(String kuerzel) {
		this.kuerzel = kuerzel;
		return this;
	}



	public SchuleAPIModel withName(String name) {
		this.name = name;
		return this;
	}



	public SchuleAPIModel withOrt(String ort) {
		this.ort = ort;
		return this;
	}



	public SchuleAPIModel withLand(String land) {
		this.land = land;
		return this;
	}



	public SchuleAPIModel withKuerzelLand(String kuerzelLand) {
		this.kuerzelLand = kuerzelLand;
		return this;
	}



	public SchuleAPIModel withAktuellAngemeldet(boolean aktuellAngemeldet) {
		this.aktuellAngemeldet = aktuellAngemeldet;
		return this;
	}



	public SchuleAPIModel withAuswertungsmodus(Auswertungsmodus auswertungsmodus) {
		this.auswertungsmodus = auswertungsmodus;
		return this;
	}



	public SchuleAPIModel withDetails(SchuleDetails details) {
		this.details = details;
		return this;
	}
}
