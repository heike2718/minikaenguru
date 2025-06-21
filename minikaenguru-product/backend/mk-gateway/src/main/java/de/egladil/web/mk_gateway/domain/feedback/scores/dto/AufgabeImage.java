// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback.scores.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AufgabeImage
 */
@Schema(description = "Daten eines Bildes")
public class AufgabeImage {

	@JsonProperty
	@Schema(description = "Breite des Images")
	private int width;

	@JsonProperty
	@Schema(description = "Höhe des Images")
	private int height;

	@JsonProperty
	@Schema(
		description = "das image-Format: 'image/png' oder 'image/svg+xml'. Kann im img-Tag verwendet werden als 'data:image/png' bzw. 'data:image/svg+xml'")
	private String format;

	@JsonProperty
	@Schema(
		description = "Base64-encodetes png Kann mit einem img src=\"data:image/png;base64- Tag angezeigt werden")
	private byte[] data;

}
