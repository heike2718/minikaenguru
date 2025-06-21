// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import java.util.function.Function;

/**
 * DoubleAsStringWithKommaMapper
 */
public class DoubleAsStringWithKommaMapper implements Function<Double, String> {

	@Override
	public String apply(final Double t) {

		String str = t.toString();

		str = str.replace('.', ',');
		return str;
	}

}
