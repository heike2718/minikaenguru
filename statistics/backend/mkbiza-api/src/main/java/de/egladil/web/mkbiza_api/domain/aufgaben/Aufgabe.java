// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Aufgabe
 */
@Schema(description = "Eine Aufgabe eines Minkänguru-Wettbewerbs")
public class Aufgabe {

	@JsonProperty
	@Schema(description = "Nummer der Aufgabe im Wettbewerb")
	private String nummer;

	@JsonProperty
	@Schema(description = "Punkte für diese Aufgabe.")
	private int punkte;

	@JsonProperty
	@Schema(description = "Strafpunkte bei falscher Lösung.")
	private String strafpunkte;

	@JsonProperty
	@Schema(description = "der korrekte Lösungsbuchstabe")
	private String loesungsbuchstabe;

	@JsonProperty
	@Schema(description = "Quelle ür eine Zitatsection")
	private String quelle;

	@JsonProperty
	@Schema(description = "Images, die angezeigt werden können. Frage und optionale Lösung")
	private Images images;

	@JsonProperty
	@Schema(description = "Statistik zu dieser Aufgabe")
	private StatistikAufgabe statistik;

	public String getNummer() {

		return nummer;
	}

	public Aufgabe withNummer(final String nummer) {

		this.nummer = nummer;
		return this;
	}

	public int getPunkte() {

		return punkte;
	}

	public Aufgabe withPunkte(final int punkte) {

		this.punkte = punkte;
		return this;
	}

	public String getLoesungsbuchstabe() {

		return loesungsbuchstabe;
	}

	public Aufgabe withLoesungsbuchstabe(final String loesungsbuchstabe) {

		this.loesungsbuchstabe = loesungsbuchstabe;
		return this;
	}

	public Images getImages() {

		return images;
	}

	public Aufgabe withImages(final Images images) {

		this.images = images;
		return this;
	}

	public String getQuelle() {

		return quelle;
	}

	public Aufgabe withQuelle(final String quelle) {

		this.quelle = quelle;
		return this;
	}

	public StatistikAufgabe getStatistik() {

		return statistik;
	}

	public void setStatistik(final StatistikAufgabe statistik) {

		this.statistik = statistik;
	}

	public void setStrafpunkte(final String strafpunkte) {

		this.strafpunkte = strafpunkte;
	}
}
