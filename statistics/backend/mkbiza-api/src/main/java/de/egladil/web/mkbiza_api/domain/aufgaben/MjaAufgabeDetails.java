// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MjaAufgabeDetails
 */
@Schema(description = "Details einer Aufgabe eines Minkänguru-Wettbewerbs aus der Archivanwendung")
public class MjaAufgabeDetails {

	@JsonProperty
	@Schema(description = "Nummer der Aufgabe im Wettbewerb")
	private String nummer;

	@JsonProperty
	@Schema(description = "Punkte für diese Aufgabe.")
	private int punkte;

	@JsonProperty
	@Schema(description = "der korrekte Lösungsbuchstabe")
	private String loesungsbuchstabe;

	@JsonProperty
	@Schema(description = "Quelle ür eine Zitatsection")
	private String quelle;

	@JsonProperty
	@Schema(description = "Images, die angezeigt werden können. Frage und optionale Lösung")
	private Images images;

	public String getNummer() {

		return nummer;
	}

	public int getPunkte() {

		return punkte;
	}

	public String getLoesungsbuchstabe() {

		return loesungsbuchstabe;
	}

	public String getQuelle() {

		return quelle;
	}

	public Images getImages() {

		return images;
	}

}
