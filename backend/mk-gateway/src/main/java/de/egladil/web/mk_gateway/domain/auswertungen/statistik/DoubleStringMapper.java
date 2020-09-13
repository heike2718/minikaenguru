// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen.statistik;

import java.util.function.Function;

/**
 * DoubleStringMapper
 */
public class DoubleStringMapper implements Function<Double, String> {

	@Override
	public String apply(final Double zahl) {

		String str = "" + zahl;
		return str.replace(".", ",");
	}

}
