// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SchuleAPIModel ist der Teil des Gui-Models für eine Schulenliste, der die Anmeldeinformation zur Schule enthält. Dieses wird
 * mit dem KatalogItem aus dem Schulkatalog zusammengeführt.
 */
public class SchuleAPIModel {

	@JsonProperty
	private String kuerzel;

	@JsonProperty
	private String name;

	@JsonProperty
	private String ort;

	@JsonProperty
	private String land;

	@JsonProperty
	private boolean aktuellAngemeldet;

	@JsonProperty
	private SchuleDetails details;

	SchuleAPIModel() {

	}

	public SchuleAPIModel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public SchuleAPIModel withDetails(final SchuleDetails details) {

		this.details = details;

		return this;
	}

	public SchuleAPIModel angemeldet() {

		this.aktuellAngemeldet = true;
		return this;
	}

	public String kuerzel() {

		return kuerzel;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public String getName() {

		return name;
	}

	public String getOrt() {

		return ort;
	}

	public String getLand() {

		return land;
	}

	public boolean isAktuellAngemeldet() {

		return aktuellAngemeldet;
	}

	public SchuleDetails getDetails() {

		return details;
	}

}
