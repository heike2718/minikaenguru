// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.util.HashMap;
import java.util.Map;

import de.egladil.web.mk_gateway.domain.statistik.Aufgabenkategorie;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * Wertungsrechner
 */
public class Wertungsrechner {

	public Wettbewerbswertung getWertung(final String wertungscode, final Klassenstufe klassenstufe) {

		if (wertungscode.length() != klassenstufe.getAnzahlAufgabenInKategorien() * 3) {

			throw new IllegalArgumentException(
				"laenge wertungscode " + wertungscode + " (" + wertungscode.length() + ") und klassenstufe " + klassenstufe
					+ " sind inkompatibel");
		}

		int punkte = klassenstufe.getStartguthaben();

		Map<Aufgabenkategorie, String> wertungscodegruppen = this.wertungscodeForKategorien(wertungscode, klassenstufe);

		for (Aufgabenkategorie kategorie : wertungscodegruppen.keySet()) {

			String code = wertungscodegruppen.get(kategorie);

			for (int i = 0; i < code.length(); i++) {

				char c = code.charAt(i);

				switch (c) {

				case 'r':
					punkte += kategorie.getPunkte();
					break;

				case 'f':
					punkte -= kategorie.getPenalty(klassenstufe);

				default:
					break;
				}
			}

		}

		int kaengurusprung = getKaengurusprung(wertungscode);

		return new Wettbewerbswertung(punkte, kaengurusprung);
	}

	int getKaengurusprung(final String wertungscode) {

		int kaengurusprung = 0;
		String r = "";

		for (int i = 0; i < wertungscode.length(); i++) {

			r += "r";

			if (wertungscode.contains(r)) {

				kaengurusprung = r.length();
			}

		}
		return kaengurusprung;
	}

	Map<Aufgabenkategorie, String> wertungscodeForKategorien(final String wertungscode, final Klassenstufe klassenstufe) {

		int anzahl = klassenstufe.getAnzahlAufgabenInKategorien();

		Map<Aufgabenkategorie, String> gruppen = new HashMap<>();

		{

			String str = wertungscode.substring(0, anzahl);
			gruppen.put(Aufgabenkategorie.LEICHT, str);
		}

		{

			String str = wertungscode.substring(anzahl, 2 * anzahl);
			gruppen.put(Aufgabenkategorie.MITTEL, str);
		}

		{

			String str = wertungscode.substring(2 * anzahl);
			gruppen.put(Aufgabenkategorie.SCHWER, str);
		}

		return gruppen;
	}

}
