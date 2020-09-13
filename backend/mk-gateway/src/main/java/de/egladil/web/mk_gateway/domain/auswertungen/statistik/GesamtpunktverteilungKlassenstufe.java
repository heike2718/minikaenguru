// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen.statistik;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * GesamtpunktverteilungKlassenstufe hat die Daten einer vollständigen Auswertung über eine Menge von Lösungszetteln der gleichen
 * Klassenstufe.
 */
@XmlRootElement(name = "gesamtpunktverteilung")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
	name = "punkte", propOrder = { "titel", "basis", "bewertung", "gesamtpunktverteilungItems", "anzahlTeilnehmer", "median",
		"rohpunktItems", "sectionEinzelergebnisse", "aufgabeErgebnisItems" })
public class GesamtpunktverteilungKlassenstufe {

	@XmlTransient
	private Klassenstufe klassenstufe;

	@XmlElement(name = "titel")
	private String titel;

	@XmlElement(name = "basis")
	private String basis;

	@XmlElement(name = "bewertung")
	private String bewertung;

	@XmlElement(name = "teilnehmerzahl")
	private int anzahlTeilnehmer;

	@XmlElement(name = "sectionEinzelergebnisse")
	private String sectionEinzelergebnisse = "Lösungen je Aufgabe";

	@XmlElement(name = "median")
	private String median;

	@XmlElement(name = "intervallItem")
	private List<GesamtpunktverteilungItem> gesamtpunktverteilungItems = new ArrayList<>();

	@XmlElement(name = "aufgabeErgebnis")
	private List<AufgabeErgebnisItem> aufgabeErgebnisItems = new ArrayList<>();

	@XmlElement(name = "rohpunktitem")
	private List<RohpunktItem> rohpunktItems = new ArrayList<>();

	GesamtpunktverteilungKlassenstufe() {

	}

	public GesamtpunktverteilungKlassenstufe(final GesamtpunktverteilungTexte texte, final GesamtpunktverteilungKlassenstufeDaten daten) {

		this.klassenstufe = daten.klassenstufe();
		this.anzahlTeilnehmer = daten.anzahlTeilnehmer();
		this.basis = texte.basis();
		this.bewertung = texte.bewertung();
		this.median = daten.median();
		this.sectionEinzelergebnisse = texte.sectionEinzelergebnisse();
		this.titel = texte.titel();
		this.gesamtpunktverteilungItems = daten.gesamtpunktverteilungItems();
		this.aufgabeErgebnisItems = daten.aufgabeErgebnisItems();
		this.rohpunktItems = daten.rohpunktItems();
	}

}
