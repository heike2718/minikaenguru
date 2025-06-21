// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.statistik.RohpunktItem;

/**
 * MkBiZaStatistikKlassenstufe
 */
@Schema(description = "statistische Daten zu einer Wettbewerbsklassenstufe")
public class MkBiZaStatistikKlassenstufe {

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
	@Schema(description = "Anzahl der Kinder, die privat teilgenommen haben")
	private int anzahlKinderPrivat;

	@JsonProperty
	@Schema(description = "fasst den Median dieser Klassenstufe und die maximal erreichtbare Anzahl Punkte zusammen")
	private MkBiZaMedianDto medianUndGesamtpunkte;

	@JsonProperty
	@Schema(description = "Histogramm der Kinder je Land, wobei PRIVAT als ein Land interpretiert wird.")
	private List<MkBiZaGruppierungsitem> kinderJeLand = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaGruppierungsitem> kinderJeTeilnahmeart = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaGruppierungsitem> kinderJeSprache = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Anzahl Kinder, die in eins der Punktintervalle in der Gesamtstatistik fallen")
	private List<MkBiZaGruppierungsitem> kinderJePunktintervall = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Rohpunktitems der Gesamtstatistik")
	private List<RohpunktItem> rohpunkte = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaStatistikAufgabe> aufgabenstatistiken = new ArrayList<>();

	public void addKinderJePunktintervall(final MkBiZaGruppierungsitem gruppierungsitem) {

		this.kinderJePunktintervall.add(gruppierungsitem);
	}

	public void setRohpunkte(final List<RohpunktItem> rohpunkte) {

		this.rohpunkte = rohpunkte;
	}

	public void addAufgabenstatistik(final MkBiZaStatistikAufgabe statistikAufgabe) {

		this.aufgabenstatistiken.add(statistikAufgabe);
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

	public void setMedianUndGesamtpunkte(final MkBiZaMedianDto medianUndGesamtpunkte) {

		this.medianUndGesamtpunkte = medianUndGesamtpunkte;
	}

	public void addKinderJeTeilnahmeart(final MkBiZaGruppierungsitem gruppierungsitem) {

		this.kinderJeTeilnahmeart.add(gruppierungsitem);
	}

	public void addKinderJeSprache(final MkBiZaGruppierungsitem gruppierungsitem) {

		this.kinderJeSprache.add(gruppierungsitem);
	}

	public void addKinderJeLand(final MkBiZaGruppierungsitem guppierungsitem) {

		this.kinderJeLand.add(guppierungsitem);
	}

	public void setBeendet(final boolean beendet) {

		this.beendet = beendet;
	}

}
