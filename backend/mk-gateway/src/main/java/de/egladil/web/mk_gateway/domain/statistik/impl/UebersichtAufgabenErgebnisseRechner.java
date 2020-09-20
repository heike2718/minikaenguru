// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.statistik.AufgabeErgebnisItem;
import de.egladil.web.mk_gateway.domain.statistik.AufgabeErgebnisRechner;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * UebersichtAufgabenErgebnisseRechner
 */
public class UebersichtAufgabenErgebnisseRechner {

	/**
	 * Erstellt die Liste mit den AufgabeErgebnisItems
	 *
	 * @param  wettbewerbId
	 * @param  klassenstufe
	 * @param  alleLoesungszettel
	 * @return
	 */
	public List<AufgabeErgebnisItem> berechneAufgabeErgebnisItems(final WettbewerbID wettbewerbId, final Klassenstufe klassenstufe, final List<Loesungszettel> alleLoesungszettel) {

		final List<AufgabeErgebnisItem> result = new ArrayList<>();

		AufgabeErgebnisRechner aufgabeErgebnsRechner = new AufgabeErgebnisRechner();

		Map<String, Integer> aufgabennummernWithWertungscodeIndex = klassenstufe
			.getAufgabennummernWithWertungscodeIndex(wettbewerbId.jahr());

		for (String aufgabennummer : aufgabennummernWithWertungscodeIndex.keySet()) {

			Integer index = aufgabennummernWithWertungscodeIndex.get(aufgabennummer);
			result.add(aufgabeErgebnsRechner.berechneAufgabeErgebnisItem(aufgabennummer, index.intValue(), alleLoesungszettel));

		}

		return result;
	}

}
