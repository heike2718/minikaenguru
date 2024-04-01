// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.wettbewerbe;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkbiza_api.domain.Klassenstufe;
import de.egladil.web.mkbiza_api.domain.dto.Gruppierungsitem;

/**
 * WettbewerbDetails
 */
@Schema(description = "Stellt die statistischen Details eine Minikänguru-Wettbewerbs zur Verfügung")
public class WettbewerbDetails {

	@JsonProperty
	@Schema(description = "Wettbewerbsjahr", example = "2020")
	private int jahr;

	@JsonProperty
	@Schema(description = "untere Schranke für die Anzahl aller Kinder, die teilgenommen haben", example = "16534")
	private int anzahlKinderGesamt;

	@JsonProperty
	@Schema(description = "Anzahl aller Privatveranstalter, die sich angemeldet hatten", example = "143")
	private long anzahlPrivatanmeldungen;

	@JsonProperty
	@Schema(description = "Anzahl aller Schulen, die sich angemeldet hatten", example = "623")
	private long anzahlSchulanmeldungen;

	@JsonProperty
	@Schema(description = "untere Schranke für die Anzahl aller Schulen, die teilgenommen haben", example = "423")
	private long teilnehmendeSchulenGesamt;

	@JsonProperty
	@Schema(description = "Klassenstufen, zu denen Daten vorliegen")
	private List<Klassenstufe> klassenstufen = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Anzahl der teilnehmenden Schulen je Land (untere Schranke)")
	private List<Gruppierungsitem> schulenJeLand = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Anzahl der teilnehmenden Kinder je Land (untere Schranke)")
	private List<Gruppierungsitem> schulkinderJeLand;

	@JsonProperty
	@Schema(description = "Anzahl der teilnehmenden Kinder je Teilnahmeart (PRIVAT/SCHULE - untere Schranke)")
	private List<Gruppierungsitem> kinderJeTeilnahmeart;

	@JsonProperty
	@Schema(description = "Anzahl der teilnehmenden Kinder je Klassenstufe (IKID/EINS/ZWEI - untere Schranke)")
	private List<Gruppierungsitem> kinderJeKlassenstufe;

	@JsonProperty
	@Schema(description = "Anzahl der teilnehmenden Kinder je Sprache (de/en - untere Schranke)")
	private List<Gruppierungsitem> kinderJeSprache;

	@JsonProperty
	@Schema(description = "Mediane je Klassenstufe")
	private List<Gruppierungsitem> medianeJeKlassenstufe;

}
