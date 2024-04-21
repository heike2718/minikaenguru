// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.klassenstufen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkbiza_api.domain.aufgaben.MkGatewayStatistikAufgabe;
import de.egladil.web.mkbiza_api.domain.dto.Gruppierungsitem;

/**
 * MkGatewayStatistikKlassenstufe. ResponsePayload der REST-API /mk-gateway/mkbiza/wettbewerbe/{jahr}/{klassenstufe}<br>
 * <br>
 * Ist das PoJo MkBiZaStatistikKlassenstufe aus mk-gateway
 */
@Schema(description = "statistische Daten zu einer Wettbewerbsklassenstufe")
public class MkGatewayStatistikKlassenstufe {

	@JsonProperty
	@Schema(description = "Jahr des Wettbewerbs")
	private String wettbewerbsjahr;

	@JsonProperty
	@Schema(description = "Klassenstufe in menschenlesbarer Form")
	private String klassenstufe;

	@JsonProperty
	@Schema(description = "Flag, ob der Wettbewerb beendet ist")
	private boolean beendet;

	@JsonProperty
	@Schema(description = "Anzahl der Kinder, zu denen Lösungszettel vorliegen")
	private int anzahlKinderGesamt;

	@JsonProperty
	private int anzahlKinderPrivat;

	@JsonProperty
	@Schema(description = "fasst den Median dieser Klassenstufe und die maximal erreichtbare Anzahl Punkte zusammen")
	private MedianDto medianUndGesamtpunkte;

	@JsonProperty
	@Schema(description = "Histogramm der Kinder je Land, wobei PRIVAT als ein Land interpretiert wird.")
	private List<Gruppierungsitem> kinderJeLand = new ArrayList<>();

	@JsonProperty
	private List<Gruppierungsitem> kinderJeTeilnahmeart = new ArrayList<>();

	@JsonProperty
	private List<Gruppierungsitem> kinderJeSprache = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Anzahl Kinder, die in eins der Punktintervalle in der Gesamtstatistik fallen")
	private List<Gruppierungsitem> kinderJePunktintervall;

	@JsonProperty
	@Schema(description = "Rohpunktitems der Gesamtstatistik")
	private List<RohpunktItem> rohpunkte;

	@JsonProperty
	private List<MkGatewayStatistikAufgabe> aufgabenstatistiken;

	public String getWettbewerbsjahr() {

		return wettbewerbsjahr;
	}

	public String getKlassenstufe() {

		return klassenstufe;
	}

	public int getAnzahlKinderGesamt() {

		return anzahlKinderGesamt;
	}

	public MedianDto getMedianUndGesamtpunkte() {

		return medianUndGesamtpunkte;
	}

	public List<Gruppierungsitem> getKinderJeLand() {

		return kinderJeLand;
	}

	public List<Gruppierungsitem> getKinderJeTeilnahmeart() {

		return kinderJeTeilnahmeart;
	}

	public List<Gruppierungsitem> getKinderJeSprache() {

		return kinderJeSprache;
	}

	public List<Gruppierungsitem> getKinderJePunktintervall() {

		return kinderJePunktintervall;
	}

	public List<RohpunktItem> getRohpunkte() {

		return rohpunkte;
	}

	public List<MkGatewayStatistikAufgabe> getAufgabenstatistiken() {

		return aufgabenstatistiken;
	}

	public boolean isBeendet() {

		return beendet;
	}

}
