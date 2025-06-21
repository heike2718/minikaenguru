// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.aufgaben;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Images
 */
@Schema(
	name = "Images",
	description = "Objekt das ein png mit dem Text für die Frage/Quizaufgabe und ein png mit dem Text für die Lösung enthält")
public class Images {

	@JsonProperty
	@Schema(
		description = "Das png mit Maßen für die Frage")
	private Image imageFrage;

	@JsonProperty
	@Schema(
		description = "Das png mit Maßen für die Lösung. Es kann null sein.")
	private Image imageLoesung;
}
