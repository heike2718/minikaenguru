// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Gruppierungsitem
 */
@Schema(description = "Gruppierungsitem - label und Anzahl")
public class Gruppierungsitem {

	@JsonProperty
	@Schema(description = "Semantik für die Anzahl, kann so als Text in einer GUI verwendet werden", example = "Klasse 1")
	private String name;

	@JsonProperty
	@Schema(description = "Anzahl Elemente mit dieser Semantik", example = "356")
	private long anzahl;

	public String getName() {

		return name;
	}

	public long getAnzahl() {

		return anzahl;
	}

	public Gruppierungsitem withName(final String name) {

		this.name = name;
		return this;
	}

	public Gruppierungsitem withAnzahl(final long anzahl) {

		this.anzahl = anzahl;
		return this;
	}

}
