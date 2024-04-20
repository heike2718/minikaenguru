// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.klassenstufen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkbiza_api.domain.aufgaben.AufgabeDetails;
import de.egladil.web.mkbiza_api.domain.dto.Gruppierungsitem;

/**
 * KlassenstufeDetails ist der Merge aus MkGatewayStatistikKlassenstufe und MjaAufgabenKlassenstufeDto
 */
@Schema(
	description = "Bei nicht beendeten Wettbewerben ist dies eine Echtzeitstatistik über die Beteiligung an dieser Klassenstufe. Ansonsten auch eine Auswertungsstatistik.",
	title = "Statistik und Bilder der Wettbewerbsaufgaben einer Klassenstufe.")
public class KlassenstufeDetails {

	@Schema(description = "Jahr des Wettbewerbs")
	@JsonProperty
	private String wettbewerbsjahr;

	@JsonProperty
	@Schema(description = "Klassenstufe in menschenlesbarer Form")
	private String klassenstufe;

	@JsonProperty
	@Schema(
		description = "Flag, ob der Wettbewerb beendet ist. Falls ja, sind die Statistiken konstant, falls nicht, kann man sie immer wieder neu laden")
	private boolean beendet;

	@JsonProperty
	@Schema(description = "Anzahl Punkte des Startpunktguthabens")
	private int startguthaben;

	@JsonProperty
	@Schema(description = "untere Schranke für die Anzahl aller Kinder, die teilgenommen haben", example = "16534")
	private int anzahlKinderGesamt;

	@JsonProperty
	@Schema(description = "untere Schranke für die Anzahl aller Kinder mit voller Punktzahl", example = "16534")
	private int anzahlKinderMitVollerPunktzahl;

	@JsonProperty
	@Schema(description = "fasst den Median dieser Klassenstufe und die maximal erreichtbare Anzahl Punkte zusammen")
	private MedianDto medianUndGesamtpunkte;

	@JsonProperty
	@Schema(description = "Histogramm der Kinder je Land, wobei PRIVAT als ein Land interpretiert wird.")
	private List<Gruppierungsitem> kinderJeLand = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Anzahl der teilnehmenden Kinder je Teilnahmeart (PRIVAT/SCHULE - untere Schranke)")
	private List<Gruppierungsitem> kinderJeTeilnahmeart;

	@JsonProperty
	@Schema(description = "Anzahl der teilnehmenden Kinder je Sprache (de/en - untere Schranke)")
	private List<Gruppierungsitem> kinderJeSprache;

	@JsonProperty
	@Schema(description = "Anzahl der teilnehmenden Kinder je Punktzahl (untere Schranke)")
	private List<Gruppierungsitem> kinderJePunktintervall = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Rohpunktitems der Gesamtstatistik")
	private List<RohpunktItem> rohpunkte = new ArrayList<>();

	@JsonProperty
	@Schema(description = "die einzelnen Aufgaben mit Lösungen")
	private List<AufgabeDetails> aufgaben = new ArrayList<>();

	@Override
	public String toString() {

		return "KlassenstufeDetails [wettbewerbsjahr=" + wettbewerbsjahr + ", klassenstufe=" + klassenstufe + ", beendet=" + beendet
			+ ", anzahlKinderGesamt=" + anzahlKinderGesamt + "]";
	}

	public void setWettbewerbsjahr(final String wettbewerbsjahr) {

		this.wettbewerbsjahr = wettbewerbsjahr;
	}

	public void setKlassenstufe(final String klassenstufe) {

		this.klassenstufe = klassenstufe;
	}

	public void setAnzahlKinderGesamt(final int anzahlKinderGesamt) {

		this.anzahlKinderGesamt = anzahlKinderGesamt;
	}

	public void setMedianUndGesamtpunkte(final MedianDto medianUndGesamtpunkte) {

		this.medianUndGesamtpunkte = medianUndGesamtpunkte;
	}

	public void setKinderJeTeilnahmeart(final List<Gruppierungsitem> kinderJeTeilnahmeart) {

		this.kinderJeTeilnahmeart = kinderJeTeilnahmeart;
	}

	public void setKinderJeSprache(final List<Gruppierungsitem> kinderJeSprache) {

		this.kinderJeSprache = kinderJeSprache;
	}

	public void setKinderJeLand(final List<Gruppierungsitem> kinderJeLand) {

		this.kinderJeLand = kinderJeLand;
	}

	public void setKinderJePunktintervall(final List<Gruppierungsitem> kinderJePunktintervall) {

		this.kinderJePunktintervall = kinderJePunktintervall;
	}

	public void setRohpunkte(final List<RohpunktItem> rohpunkte) {

		this.rohpunkte = rohpunkte;
	}

	public void setAufgaben(final List<AufgabeDetails> aufgaben) {

		this.aufgaben = aufgaben;
	}

	public void setBeendet(final boolean beendet) {

		this.beendet = beendet;
	}

	public void setStartguthaben(final int startguthaben) {

		this.startguthaben = startguthaben;
	}

	public void setAnzahlKinderMitVollerPunktzahl(final int anzahlKinderMitVollerPunktzahl) {

		this.anzahlKinderMitVollerPunktzahl = anzahlKinderMitVollerPunktzahl;
	}

}
