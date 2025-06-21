// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PunkteStringMapper
 */
public class PunkteStringMapper implements Function<Integer, String> {

	private static final Logger LOG = LoggerFactory.getLogger(PunkteStringMapper.class);

	@Override
	public String apply(final Integer punkte) {

		if (punkte.intValue() == 0) {

			return "0";
		}
		final String punkteString = String.valueOf(punkte);
		final int indexKomma = punkteString.length() - 2;
		final String nachkommastellen = punkteString.substring(indexKomma, punkteString.length());
		final String vorkommastellen = punkteString.substring(0, indexKomma);

		final String text = vorkommastellen + "," + nachkommastellen;

		LOG.debug("rein={}, raus={}", punkte, text);
		return text;
	}

}
