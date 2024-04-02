// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MinikaenguruAufgabe
 */
@Schema(description = "Eine Aufgabe eines Minkänguru-Wettbewerbs")
public class MinikaenguruAufgabe {

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

	public MinikaenguruAufgabe withNummer(final String nummer) {

		this.nummer = nummer;
		return this;
	}

	public int getPunkte() {

		return punkte;
	}

	public MinikaenguruAufgabe withPunkte(final int punkte) {

		this.punkte = punkte;
		return this;
	}

	public String getLoesungsbuchstabe() {

		return loesungsbuchstabe;
	}

	public MinikaenguruAufgabe withLoesungsbuchstabe(final String loesungsbuchstabe) {

		this.loesungsbuchstabe = loesungsbuchstabe;
		return this;
	}

	public Images getImages() {

		return images;
	}

	public MinikaenguruAufgabe withImages(final Images images) {

		this.images = images;
		return this;
	}

	public String getQuelle() {

		return quelle;
	}

	public MinikaenguruAufgabe withQuelle(final String quelle) {

		this.quelle = quelle;
		return this;
	}
}
