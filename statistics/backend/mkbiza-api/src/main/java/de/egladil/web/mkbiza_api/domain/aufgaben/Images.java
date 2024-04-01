// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

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
		description = "Base64-encodetes png mit dem Text der Frage/Quizaufgabe. Kann mit einem img src=\"data:image/png;base64- Tag angezeigt werden")
	private byte[] imageFrage;

	@JsonProperty
	@Schema(
		description = "Base64-encodetes png mit dem Text der Lösung. Kann mit einem img src=\"data:image/png;base64- Tag angezeigt werden")
	private byte[] imageLoesung;

	public byte[] getImageFrage() {

		return imageFrage;
	}

	public Images withImageFrage(final byte[] imageFrage) {

		this.imageFrage = imageFrage;
		return this;
	}

	public byte[] getImageLoesung() {

		return imageLoesung;
	}

	public Images withImageLoesung(final byte[] imageLoesung) {

		this.imageLoesung = imageLoesung;
		return this;
	}
}
