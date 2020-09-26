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
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungItem;
import de.egladil.web.mk_gateway.domain.statistik.Punktintervall;
import de.egladil.web.mk_gateway.domain.statistik.comparators.GesamtpunktverteilungItemDescendingComparator;
import de.egladil.web.mk_gateway.domain.statistik.punktintervalle.PunktintervallStrategy;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * UebersichtGesamtpunktverteilungItemsRechner
 */
public class UebersichtGesamtpunktverteilungItemsRechner {

	/**
	 * Erstellt die Liste mit den GesamtpunktverteilungItems.
	 *
	 * @return List
	 */
	public List<GesamtpunktverteilungItem> berechneGesamtpunktverteilungItems(final WettbewerbID wettbewerbId, final Klassenstufe klassenstufe, final List<Loesungszettel> loesungszettelKLassenstufe) {

		Map<Punktintervall, Integer> punktintervallKlassen = getPunktintervallklassen(wettbewerbId, klassenstufe,
			loesungszettelKLassenstufe);

		List<GesamtpunktverteilungItem> result = new ArrayList<>();

		for (Punktintervall punktintervall : punktintervallKlassen.keySet()) {

			Integer anzahlLoesungezettelImPunktintervall = punktintervallKlassen.get(punktintervall);

			GesamtpunktverteilungItem item = new GesamtpunktverteilungItem()
				.withPunktintervall(punktintervall)
				.withAnzahl(anzahlLoesungezettelImPunktintervall);

			result.add(item);

		}

		Collections.sort(result, new GesamtpunktverteilungItemDescendingComparator());

		return result;
	}

	Map<Punktintervall, Integer> getPunktintervallklassen(final WettbewerbID wettbewerbId, final Klassenstufe klassenstufe, final List<Loesungszettel> loesungszettelKlassenstufe) {

		PunktintervallStrategy punktintervalStrategy = PunktintervallStrategy.createStrategy(klassenstufe, wettbewerbId);

		List<Punktintervall> punktintervalle = punktintervalStrategy.getPunktintervalleDescending();

		final Map<Punktintervall, Integer> punktintervallklassen = new HashMap<>();

		punktintervalle.forEach(intervall -> {

			List<Loesungszettel> loesungszettelInIntervall = loesungszettelKlassenstufe.stream()
				.filter(lz -> intervall.contains(lz.punkte())).collect(Collectors.toList());

			punktintervallklassen.put(intervall, Integer.valueOf(loesungszettelInIntervall.size()));
		});

		return punktintervallklassen;
	}

}
