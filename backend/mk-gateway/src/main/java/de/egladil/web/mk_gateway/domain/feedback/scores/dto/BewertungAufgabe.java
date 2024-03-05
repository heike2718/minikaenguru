// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback.scores.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.domain.statistik.Aufgabenkategorie;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * BewertungAufgabe
 */
@Schema(
	description = "Einzelbewertungen einer gegebenen Aufgabe. Kontext ergibt sich aus der Einbettung in BewertungsbogenKlassenstufe")
public class BewertungAufgabe {

	@JsonProperty
	@Schema(description = "Aufgabennummer", example = "B-3", required = true)
	@Pattern(regexp = "^[ABC\\-\\d]{3}$", message = "nummer enthält ungültige Zeichen oder ist nicht genau 3 Zeichen lang")
	@NotNull
	private String nummer;

	@JsonProperty
	@Schema(
		description = "Punkte für Angemessenheit des Schwierigkeitsgrads (0 - 3, 0 = keine Bewertung, 1 = zu leicht, 2 = angemessen, 3 = zu schwer)",
		example = "2")
	private int scoreSchwierigkeitsgrad;

	@JsonProperty
	@Schema(
		description = "Kategorie, in die die Lehrperson diese Aufgabe eingeordnet hätte",
		example = "C")
	@Pattern(regexp = "^[ABC]{1}$", message = "empfohleneKategorie enthält ungültige Zeichen oder ist nicht genau 1 Zeichen lang")
	private Aufgabenkategorie empfohleneKategorie;

	@JsonProperty
	@Schema(
		description = "Punkte für Kompatibiliät mit dem Lehrplan des Schwierigkeitsgrads (-1, 0 oder 1, -1 = passt nicht, 0 = keine Bewertung, 1 = passt)",
		example = "2")
	private int scoreLehrplankompatibilitaet;

	@JsonProperty
	@Schema(
		description = "Punkte für Verständlichkeit der Aufgabe (-1, 0 oder 1, -1 = nicht gut verständlich, 0 = keine Bewertung, 1 = verständlich)",
		example = "2")
	private int scoreVerstaendlichkeit;

	@JsonProperty
	@Schema(description = "ein Freitext für eigene Kommentare", example = "Multiplikation ist noch nicht Teil des Lehrplans.")
	@StringLatin
	@Size(max = 500, message = "maximal 500 Zeichen")
	private String freitext;

}
