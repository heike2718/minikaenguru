// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StringPunkteMapper
 */
public class StringPunkteMapper implements Function<String, Integer> {

	private static Logger LOGGER = LoggerFactory.getLogger(StringPunkteMapper.class);

	@Override
	public Integer apply(final String punkte) {

		if (StringUtils.isBlank(punkte)) {

			return null;
		}

		if (punkte.length() > 5) {

			return null;
		}

		String ohneKomma = punkte.replaceAll(",", ".");

		Double converted = null;

		try {

			converted = Double.valueOf(ohneKomma);
		} catch (NumberFormatException e) {

			LOGGER.warn("Eingabe = " + punkte);

			return null;
		}

		return Double.valueOf(converted * 100).intValue();
	}

}
