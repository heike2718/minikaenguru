// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.impl;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelPunkteComparator;

/**
 * LoesungszettelMedianMapper
 */
public class LoesungszettelMedianMapper implements Function<java.util.List<Loesungszettel>, Double> {

	private static final Logger LOG = LoggerFactory.getLogger(LoesungszettelMedianMapper.class);

	@Override
	public Double apply(final List<Loesungszettel> alleLoesungszettel) {

		if (alleLoesungszettel == null) {

			throw new IllegalArgumentException("alleLoseungszettel darf nicht null sein");
		}

		if (alleLoesungszettel.isEmpty()) {

			throw new IllegalArgumentException("alleLoseungszettel darf nicht leer sein");
		}

		Collections.sort(alleLoesungszettel, new LoesungszettelPunkteComparator());

		int anzahl = alleLoesungszettel.size();
		double result = 0;

		if (anzahl % 2 == 0) {

			int erstes = alleLoesungszettel.get(alleLoesungszettel.size() / 2 - 1).punkte();
			int zweites = alleLoesungszettel.get(alleLoesungszettel.size() / 2).punkte();

			LOG.debug("Median zwischen {} und {}", erstes, zweites);
			result = Double.valueOf((erstes + zweites)) / 200;
		} else {

			int index = Math.floorDiv(anzahl, 2);
			LOG.debug("Median bei index {}", index);
			result = Double.valueOf(alleLoesungszettel.get(index).punkte()) / 100;
		}

		return result;
	}

}
