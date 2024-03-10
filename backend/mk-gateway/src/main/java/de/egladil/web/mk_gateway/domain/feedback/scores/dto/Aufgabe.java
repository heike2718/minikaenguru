// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback.scores.dto;

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
	@Schema(description = "der korrekte Lösungsbuchstabe")
	private String loesungsbuchstabe;

	@JsonProperty
	@Schema(description = "Quelle ür eine Zitatsection")
	private String quelle;

	@JsonProperty
	@Schema(description = "AufgabeImages, die angezeigt werden können. Frage und optionale Lösung")
	private AufgabeImages images;

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

	public AufgabeImages getImages() {

		return images;
	}

	public Aufgabe withImages(final AufgabeImages images) {

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
}
