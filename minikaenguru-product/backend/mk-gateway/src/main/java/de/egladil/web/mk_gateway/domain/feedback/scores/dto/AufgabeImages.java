// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback.scores.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AufgabeImages
 */
@Schema(
	name = "AufgabeImages",
	description = "Objekt das ein png mit dem Text für die Frage/Quizaufgabe und ein png mit dem Text für die Lösung enthält")
public class AufgabeImages {

	@JsonProperty
	@Schema(
		description = "Das png mit Maßen für die Frage")
	private AufgabeImage imageFrage;

	@JsonProperty
	@Schema(
		description = "Das png mit Maßen für die Lösung. Es kann null sein.")
	private AufgabeImage imageLoesung;
}
