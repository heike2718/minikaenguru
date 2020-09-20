// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.statistik.RohpunktItem;
import de.egladil.web.mk_gateway.domain.statistik.comparators.RohpunktItemDescendingComparator;

/**
 * UbersichtRohpunktItemsRechner
 */
public class UbersichtRohpunktItemsRechner {

	/**
	 * @return
	 */
	public List<RohpunktItem> berechneRohpunktItems(final List<Loesungszettel> alleLoesungszettel) {

		final List<RohpunktItem> result = new ArrayList<>();

		final Map<Integer, List<Loesungszettel>> punktklassen = soriereNachPunktklassen(alleLoesungszettel);

		for (Integer punkte : punktklassen.keySet()) {

			List<Loesungszettel> punktklasse = punktklassen.get(punkte);

			RohpunktItem item = new RohpunktItem().withAnzahl(punktklasse.size()).withPunkten(punkte);
			result.add(item);
		}

		Collections.sort(result, new RohpunktItemDescendingComparator());
		return result;
	}

	/**
	 * @param  alleLoesungszettel
	 * @return
	 */
	Map<Integer, List<Loesungszettel>> soriereNachPunktklassen(final List<Loesungszettel> alleLoesungszettel) {

		final Map<Integer, List<Loesungszettel>> result = new HashMap<>();

		for (Loesungszettel loesungszettel : alleLoesungszettel) {

			Integer punkte = loesungszettel.punkte();

			List<Loesungszettel> loesungszettelKlasse = result.getOrDefault(punkte, new ArrayList<>());
			loesungszettelKlasse.add(loesungszettel);
			result.put(punkte, loesungszettelKlasse);
		}

		return result;
	}

}
