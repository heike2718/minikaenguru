// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import java.util.function.BiFunction;

/**
 * ProzentRechner
 */
public class ProzentRechner implements BiFunction<Integer, Integer, Double> {

	@Override
	public Double apply(final Integer anteil, final Integer gesamtzahl) {

		return Double.valueOf(anteil) / Double.valueOf(gesamtzahl) * 100.0;
	}

}
