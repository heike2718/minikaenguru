// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.List;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.statistik.functions.DoubleStringMapper;
import de.egladil.web.mk_gateway.domain.statistik.impl.LoesungszettelMedianMapper;

/**
 * MedianRechner
 */
public class MedianRechner {

	public String berechneMedian(final List<Loesungszettel> loesungszettel) {

		Double median = new LoesungszettelMedianMapper().apply(loesungszettel);

		return new DoubleStringMapper().apply(median);
	}

}
