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
	private boolean aktuellAngemeldet;

	SchuleAPIModel() {

	}

	public SchuleAPIModel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public SchuleAPIModel angemeldet() {

		this.aktuellAngemeldet = true;
		return this;
	}

}
