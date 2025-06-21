// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;

/**
 * RundenCommand
 */
public class RundenCommand implements BiFunction<Double, Integer, Double> {

	@Override
	public Double apply(final Double zuRundendeZahl, final Integer anzahlNachkommastellen) {

		if (anzahlNachkommastellen < 0) {

			throw new IllegalArgumentException("anzahlNachkommastellen muss nichtnegativ sein.");
		}

		return new BigDecimal(zuRundendeZahl).setScale(anzahlNachkommastellen, RoundingMode.HALF_UP).doubleValue();
	}

}
