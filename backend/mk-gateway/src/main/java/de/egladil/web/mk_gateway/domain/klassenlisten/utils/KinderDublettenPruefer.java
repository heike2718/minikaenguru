// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.klassenlisten.impl.KindImportDaten;

/**
 * KinderDublettenPruefer
 */
public class KinderDublettenPruefer {

	/**
	 * Prüft die Importdaten klassenweise auf Dubletten. Gefundene Dubletten werden markiert, damit sie mit dem dublettePruefen-Flag
	 * in die Datenbank kommen.
	 *
	 * @param  importDaten
	 *                     List
	 * @return             int die Anzhal an Dubletten in der gesamten Importdatei.
	 */
	public int pruefeUndMarkiereDublettenImportDaten(final List<KindImportDaten> importDaten) {

		Map<String, List<KindImportDaten>> klassenKinderMap = new HashMap<>();

		for (KindImportDaten daten : importDaten) {

			KindEditorModel kind = daten.getKindRequestData().kind();

			List<KindImportDaten> kinderInKlasse = klassenKinderMap.getOrDefault(kind.klasseUuid(), new ArrayList<>());
			kinderInKlasse.add(daten);
		}

		int anzahlDubletten = 0;

		for (List<KindImportDaten> kinder : klassenKinderMap.values()) {

			anzahlDubletten += this.markiereDublettenInEinerKlasse(kinder);
		}

		return anzahlDubletten;
	}

	int markiereDublettenInEinerKlasse(final List<KindImportDaten> kinderInKlasse) {

		List<KindImportDaten> gepruefte = new ArrayList<>();
		int anzahlDubletten = 0;

		for (KindImportDaten daten : kinderInKlasse) {

			boolean dublette = this.dubletteGefunden(gepruefte, daten);

			if (dublette) {

				daten.setDublettePruefen(true);
				anzahlDubletten++;
				gepruefte.add(daten);
			}
		}

		return anzahlDubletten;
	}

	boolean dubletteGefunden(final List<KindImportDaten> gepruefteKinder, final KindImportDaten neuesKind) {

		for (KindImportDaten geprueftesKind : gepruefteKinder) {

			if (geprueftesKind.equals(neuesKind)) {

				return true;
			}
		}

		return false;
	}
}
