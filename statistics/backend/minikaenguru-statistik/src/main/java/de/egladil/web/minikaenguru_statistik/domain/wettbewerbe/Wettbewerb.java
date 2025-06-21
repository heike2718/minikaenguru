// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.wettbewerbe;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.minikaenguru_statistik.domain.dto.Gruppierungsitem;

/**
 * Wettbewerb
 */
@Schema(description = "Daten eines Wettbewerbs, der ausgewählt werden kann")
public class Wettbewerb {

	@JsonProperty
	@Schema(description = "Wettbewerbsjahr", examples = "2020")
	private int jahr;

	@JsonProperty
	@Schema(description = "Status des Wettbewerbs", examples = "BEENDET")
	private StatusWettbewerb status;

	@JsonProperty
	@Schema(description = "Anzahl der Kinder (ist eine untere Schranke)", examples = "12524")
	private long anzahlKinder;

	@JsonProperty
	@Schema(description = "Kinder je Klassenstufe")
	private List<Gruppierungsitem> kinderJeKlassenstufe;

	@JsonProperty
	@Schema(description = "Mediane je Klassenstufe")
	private List<Gruppierungsitem> medianeJeKlassenstufe;

	@JsonProperty
	@Schema(description = "Farben, mit denen dieser Wettbewerb in line-Grafik gezeichnet werden soll-")
	private WettbewerbColors colors;

	@JsonProperty
	@Schema(description = "Je Wettbewerbswoche wird die Anzahl der Lösungszettel kumuliert, so dass eine monoton wachsende Anzahl an Lösungszetteln für ein Liniendiagramm entsteht.")
	private List<Gruppierungsitem> kumulierteLoesungszettelJeWoche;
}
