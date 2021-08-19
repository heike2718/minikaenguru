// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.egladil.web.mk_gateway.domain.kinder.Dublettenpruefer;
import de.egladil.web.mk_gateway.domain.kinder.KindAdaptable;
import de.egladil.web.mk_gateway.domain.kinder.KindAdapter;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.klassenlisten.impl.KindImportDaten;

/**
 * ImportDublettenPruefer
 */
public class ImportDublettenPruefer {

	private final KindAdapter kindAdapter = new KindAdapter();

	private final Dublettenpruefer dublettenpruefer = new Dublettenpruefer();

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

			if (daten.getKindRequestData() != null) {

				KindEditorModel kind = daten.getKindRequestData().kind();

				List<KindImportDaten> kinderInKlasse = klassenKinderMap.getOrDefault(kind.klasseUuid(), new ArrayList<>());
				kinderInKlasse.add(daten);
				klassenKinderMap.put(kind.klasseUuid(), kinderInKlasse);
			}
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

			}
			gepruefte.add(daten);
		}

		return anzahlDubletten;
	}

	boolean dubletteGefunden(final List<KindImportDaten> gepruefteKinder, final KindImportDaten neuesKind) {

		KindAdaptable neuesKindAdapted = kindAdapter.adaptKindImportDaten(neuesKind);

		for (KindImportDaten geprueftesKind : gepruefteKinder) {

			KindAdaptable geprueftesKindAdapted = kindAdapter.adaptKindImportDaten(geprueftesKind);

			if (dublettenpruefer.apply(geprueftesKindAdapted, neuesKindAdapted)) {

				geprueftesKind.setDublettePruefen(true);
				return true;
			}
		}

		return false;
	}
}