// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.klassenstufen;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RohpunktItem
 */
@Schema(
	title = "Ein Rohpunktitem für die Gesamtstatistik",
	description = "enthält eine Punktzahl als String, die Anzahl der Lösungszettel mit dieser Punktzahl sowie den Prozentrang dieser Punktzahl.")
public class RohpunktItem {

	@JsonProperty
	private String punkte;

	@JsonProperty
	private String anzahl;

	@JsonProperty
	private String prozentrang;

}
