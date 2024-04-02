// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.wettbewerbe;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkbiza_api.domain.dto.Gruppierungsitem;

/**
 * Wettbewerb
 */
@Schema(description = "Daten eines Wettbewerbs, der ausgewählt werden kann")
public class Wettbewerb {

	@JsonProperty
	@Schema(description = "Wettbewerbsjahr", example = "2020")
	private int jahr;

	@JsonProperty
	@Schema(description = "Status des Wettbewerbs", example = "BEENDET")
	private StatusWettbewerb status;

	@JsonProperty
	@Schema(description = "Anzahl der Kinder (ist eine untere Schranke)", example = "12524")
	private long anzahlKinder;

	@JsonProperty
	@Schema(description = "Mediane je Klassenstufe")
	private List<Gruppierungsitem> medianeJeKlassenstufe;

}
