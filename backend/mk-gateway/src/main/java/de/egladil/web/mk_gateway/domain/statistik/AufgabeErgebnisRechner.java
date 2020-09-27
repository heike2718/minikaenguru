// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.List;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.statistik.functions.ProzentRechner;
import de.egladil.web.mk_gateway.domain.statistik.functions.RundenCommand;

/**
 * AufgabeErgebnisRechner
 */
public class AufgabeErgebnisRechner {

	private static final int ANZAHL_NACHKOMMASTELLEN = 2;

	/**
	 * Zählt die Anzahl richtig, falsch und nicht gelöster Aufgaben der gegebenen Aufgabennummer
	 *
	 * @param  aufgabennummer
	 *                                    String die Nummer der Aufgabe.
	 * @param  wertungscodePosition:
	 *                                    int die Position des Zeichens im Wertungscode-String.
	 * @param  loesungszettelKlassenstufe
	 *                                    List die Lösungszettel einer Klassenstufe im Auswertungsjahr
	 * @return                            AufgabeErgebnisItem
	 */
	public AufgabeErgebnisItem berechneAufgabeErgebnisItem(final String aufgabennummer, final int wertungscodePosition, final List<Loesungszettel> loesungszettelKlassenstufe) {

		int anzahlRichtig = 0;
		int anzahlNicht = 0;
		int anzahlFalsch = 0;

		for (Loesungszettel loesungszettel : loesungszettelKlassenstufe) {

			String wertungscode = loesungszettel.rohdaten().wertungscode();
			char[] wertungscodeChars = wertungscode.toCharArray();

			if (wertungscodePosition <= wertungscodeChars.length) {

				Wertung wertung = Wertung
					.valueOfStringIgnoringCase(String.valueOf(wertungscodeChars[wertungscodePosition]));

				switch (wertung) {

				case f:
					anzahlFalsch++;
					break;

				case n:
					anzahlNicht++;
					break;

				case r:
					anzahlRichtig++;
					break;

				default:
					break;
				}

			}

		}

		AufgabeErgebnisItem item = new AufgabeErgebnisItem()
			.withNummer(aufgabennummer)
			.withIndex(wertungscodePosition)
			.withAnzahlFalschGeloest(anzahlFalsch)
			.withAnzahlNichtGeloest(anzahlNicht)
			.withAnzahlRichtigGeloest(anzahlRichtig);

		return this.addAnteile(item, loesungszettelKlassenstufe.size());
	}

	private AufgabeErgebnisItem addAnteile(final AufgabeErgebnisItem item, final int anzahlLoesungszettel) {

		RundenCommand runder = new RundenCommand();
		ProzentRechner prozentrechner = new ProzentRechner();
		item.withAnteilFalschGeloest(
			runder.apply(prozentrechner.apply(item.anzahlFalschGeloest(), anzahlLoesungszettel), ANZAHL_NACHKOMMASTELLEN))
			.withAnteilNichtGeloest(
				runder.apply(prozentrechner.apply(item.anzahlNichtGeloest(), anzahlLoesungszettel), ANZAHL_NACHKOMMASTELLEN))
			.withAnteilRichtigGeloest(
				runder.apply(prozentrechner.apply(item.anzahlRichtigGeloest(), anzahlLoesungszettel), ANZAHL_NACHKOMMASTELLEN));

		return item;
	}

}
