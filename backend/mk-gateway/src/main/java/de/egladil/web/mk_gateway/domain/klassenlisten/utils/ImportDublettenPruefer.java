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
import de.egladil.web.mk_gateway.domain.klassenlisten.KindImportVO;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenimportZeile;
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
	 * @return             List mit dem warn-Text, falls Dubletten gefunden wurden.
	 */
	public List<KindImportVO> pruefeUndMarkiereDublettenImportDaten(final List<KindImportVO> importDaten) {

		Map<String, List<KindImportVO>> klassenKinderMap = new HashMap<>();

		List<KindImportVO> result = new ArrayList<>();

		for (KindImportVO kindImportDaten : importDaten) {

			KindImportDaten daten = kindImportDaten.getKindImportDaten();

			if (daten.getKindRequestData() != null) {

				KindEditorModel kind = daten.getKindRequestData().kind();

				List<KindImportVO> kinderInKlasse = klassenKinderMap.getOrDefault(kind.klasseUuid(),
					new ArrayList<>());
				kinderInKlasse.add(kindImportDaten);
				klassenKinderMap.put(kind.klasseUuid(), kinderInKlasse);
			}

		}

		for (List<KindImportVO> kinder : klassenKinderMap.values()) {

			result.addAll(this.markiereDublettenInEinerKlasse(kinder));
		}

		return result;
	}

	List<KindImportVO> markiereDublettenInEinerKlasse(final List<KindImportVO> kinderInKlasse) {

		List<KindImportVO> result = new ArrayList<>();
		List<KindImportDaten> gepruefte = new ArrayList<>();

		for (KindImportVO kindImportVO : kinderInKlasse) {

			KindImportDaten daten = kindImportVO.getKindImportDaten();
			KlassenimportZeile zeile = kindImportVO.getImportZeile();

			boolean dublette = this.dubletteGefunden(gepruefte, daten);

			if (dublette) {

				// kindImportVO.setDublettePruefen(true);
				kindImportVO.setWarnungDublette(
					"Zeile " + zeile.getIndex() + ": " + kindImportVO.getImportRohdaten() + ": In Klasse " + zeile.getKlasse()
						+ " gibt es bereits ein Kind mit diesem Namen und dieser Klassenstufe");

			}
			result.add(kindImportVO);
			gepruefte.add(daten);
		}

		return result;
	}

	boolean dubletteGefunden(final List<KindImportDaten> gepruefteKinder, final KindImportDaten neuesKind) {

		KindAdaptable neuesKindAdapted = kindAdapter.adaptKindImportDaten(neuesKind);

		for (KindImportDaten geprueftesKind : gepruefteKinder) {

			KindAdaptable geprueftesKindAdapted = kindAdapter.adaptKindImportDaten(geprueftesKind);

			if (dublettenpruefer.apply(geprueftesKindAdapted, neuesKindAdapted)) {

				return true;
			}
		}

		return false;
	}
}
