// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback.scores.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.domain.feedback.scores.Schriftart;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * BewertungsbogenKlassenstufe
 */
@Schema(
	description = "Daten eines Bewertungsbogens für eine einzelne Klassenstufe. Zuordnung zum Wettbewerb erfolgt über den jeweils eindeutig bestimmten aktuellen Wettbewerb.")
public class BewertungsbogenKlassenstufe {

	@JsonProperty
	@Schema(description = "Klassenstufe", example = "ZWEI", required = true)
	@Pattern(regexp = "^IKID|EINS|ZWEI$", message = "klassenstufe muss einen der Werte IKID, EINS oder ZWEI haben")
	@NotNull
	private Klassenstufe klassenstufe;

	@JsonProperty
	@Schema(description = "Punkte für Spaßfaktor für Kinder von 0 bis 5 (0 = keine Bewertung)", example = "4")
	private int scoreSpassfaktor;

	@JsonProperty
	@Schema(
		description = "Punkte für Zufriedenheit der Lehrperson mit den Aufgaben insgesamt von 0 bis 5 (0 = keine Bewertung)",
		example = "1")
	private int scoreZufriedenheit;

	@JsonProperty
	@Schema(
		description = "Zum Ausdrucken gewählte Schriftart",
		example = "FIBEL_NORD")
	private Schriftart schriftart;

	@JsonProperty
	@Schema(description = "Einzelbewertungen der Aufgaben")
	@Valid
	private List<BewertungAufgabe> bewertungenAufgaben;

	@JsonProperty
	@Schema(
		description = "ein Freitext für eigene Kommentare",
		example = "Die Aufgaben waren dieses Jahr viel schwerer als im vergangenen Jahr.")
	@StringLatin
	@Size(max = 500, message = "maximal 500 Zeichen")
	private String freitext;

	public Klassenstufe getKlassenstufe() {

		return klassenstufe;
	}

	public BewertungsbogenKlassenstufe withKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public int getScoreSpassfaktor() {

		return scoreSpassfaktor;
	}

	public BewertungsbogenKlassenstufe withScoreSpassfaktor(final int scoreSpassfaktor) {

		this.scoreSpassfaktor = scoreSpassfaktor;
		return this;
	}

	public int getScoreZufriedenheit() {

		return scoreZufriedenheit;
	}

	public BewertungsbogenKlassenstufe withScoreZufriedenheit(final int scoreZufriedenheit) {

		this.scoreZufriedenheit = scoreZufriedenheit;
		return this;
	}

	public List<BewertungAufgabe> getBewertungenAufgaben() {

		return bewertungenAufgaben;
	}

	public BewertungsbogenKlassenstufe withBewertungenAufgaben(final List<BewertungAufgabe> bewertungenAufgaben) {

		this.bewertungenAufgaben = bewertungenAufgaben;
		return this;
	}

	public String getFreitext() {

		return freitext;
	}

	public BewertungsbogenKlassenstufe withFreitext(final String freitext) {

		this.freitext = freitext;
		return this;
	}

	public Schriftart getSchriftart() {

		return schriftart;
	}

	public BewertungsbogenKlassenstufe withSchriftart(final Schriftart schriftart) {

		this.schriftart = schriftart;
		return this;
	}
}
