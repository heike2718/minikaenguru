// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback.scores.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.StringLatin;
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
}
