// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Gruppierungsitem
 */
@Schema(description = "Gruppierungsitem - Label und Anzahl. Das Label bezieht sich auf die Grafik.")
public class Gruppierungsitem {

	@JsonProperty
	@Schema(description = "Semantik für die Anzahl, kann so als Text in einer GUI verwendet werden", examples = "Klasse 1")
	private String name;

	@JsonProperty
	@Schema(description = "Anzahl Elemente mit dieser Semantik", examples = "356")
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
