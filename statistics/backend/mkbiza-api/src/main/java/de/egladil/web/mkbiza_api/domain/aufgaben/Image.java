// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image
 */
@Schema(description = "base64 encodete Daten eines png sowie Breite und Höhe")
public class Image {

	@JsonProperty
	@Schema(description = "Breite des Images")
	private int width;

	@JsonProperty
	@Schema(description = "Höhe des Images")
	private int height;

	@JsonProperty
	@Schema(
		description = "Base64-encodetes png Kann mit einem img src=\"data:image/png;base64- Tag angezeigt werden")
	private byte[] data;

}
