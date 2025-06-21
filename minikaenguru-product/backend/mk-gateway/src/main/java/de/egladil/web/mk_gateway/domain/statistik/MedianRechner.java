// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.Collections;
import java.util.List;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.statistik.functions.DoubleStringMapper;
import de.egladil.web.mk_gateway.domain.statistik.impl.LoesungszettelMedianMapper;

/**
 * MedianRechner
 */
public class MedianRechner {

	/**
	 * @param  loesungszettel
	 * @return
	 */
	public String berechneMedian(final List<Loesungszettel> loesungszettel) {

		Double median = new LoesungszettelMedianMapper().apply(loesungszettel);

		// double median = calculateMedian(loesungszettel);

		return new DoubleStringMapper().apply(median);
	}

	double calculateMedian(final List<Loesungszettel> allLoesungszettel) {

		List<Integer> allPoints = allLoesungszettel.stream().map(l -> l.punkte()).toList();
		Collections.sort(allPoints);

		int anzahl = allPoints.size();
		double result = 0;

		if (anzahl % 2 == 0) {

			int erstes = allPoints.get(allPoints.size() / 2 - 1);
			int zweites = allPoints.get(allPoints.size() / 2);

			result = Double.valueOf((erstes + zweites)) / 200;
		} else {

			int index = Math.floorDiv(anzahl, 2);
			result = Double.valueOf(allPoints.get(index)) / 100;
		}

		return result;
	}
}
