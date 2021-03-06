// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * GesamtpunktverteilungKlassenstufeDaten enthält die Daten einer GesamtpunktverteilungKlassenstufe für eine gegebene Klassenstufe.
 */
public class GesamtpunktverteilungKlassenstufeDaten {

	@JsonProperty
	private Klassenstufe klassenstufe;

	@JsonProperty
	private int anzahlTeilnehmer;

	@JsonProperty
	private String median;

	@JsonIgnore
	private List<GesamtpunktverteilungItem> gesamtpunktverteilungItems = new ArrayList<>();

	@JsonIgnore
	private List<AufgabeErgebnisItem> aufgabeErgebnisItems = new ArrayList<>();

	@JsonIgnore
	private List<RohpunktItem> rohpunktItems = new ArrayList<>();

	public Klassenstufe klassenstufe() {

		return klassenstufe;
	}

	public GesamtpunktverteilungKlassenstufeDaten withKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public int anzahlTeilnehmer() {

		return anzahlTeilnehmer;
	}

	public GesamtpunktverteilungKlassenstufeDaten withAnzahlTeilnehmer(final int anzahlTeilnehmer) {

		this.anzahlTeilnehmer = anzahlTeilnehmer;
		return this;
	}

	public String median() {

		return median;
	}

	public GesamtpunktverteilungKlassenstufeDaten withMedian(final String median) {

		this.median = median;
		return this;
	}

	public List<GesamtpunktverteilungItem> gesamtpunktverteilungItems() {

		return gesamtpunktverteilungItems;
	}

	public List<AufgabeErgebnisItem> aufgabeErgebnisItems() {

		return aufgabeErgebnisItems;
	}

	public List<RohpunktItem> rohpunktItems() {

		return rohpunktItems;
	}

	@Override
	public int hashCode() {

		return Objects.hash(klassenstufe);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		GesamtpunktverteilungKlassenstufeDaten other = (GesamtpunktverteilungKlassenstufeDaten) obj;
		return klassenstufe == other.klassenstufe;
	}

	public GesamtpunktverteilungKlassenstufeDaten withGesamtpunktverteilungItems(final List<GesamtpunktverteilungItem> gesamtpunktverteilungItems) {

		this.gesamtpunktverteilungItems = gesamtpunktverteilungItems;
		return this;
	}

	public GesamtpunktverteilungKlassenstufeDaten withAufgabeErgebnisItems(final List<AufgabeErgebnisItem> aufgabeErgebnisItems) {

		this.aufgabeErgebnisItems = aufgabeErgebnisItems;
		return this;
	}

	public GesamtpunktverteilungKlassenstufeDaten withRohpunktItems(final List<RohpunktItem> rohpunktItems) {

		this.rohpunktItems = rohpunktItems;
		return this;
	}

}
