// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LengthTester
 */
public class LengthTester {

	private static final Logger LOG = LoggerFactory.getLogger(LengthTester.class);

	/**
	 * @param  name
	 *                                  String
	 * @param  minimalFontSize
	 *                                  int be mehreren möglichen font sizes die kleinstmögliche
	 * @throws IllegalArgumentException
	 *                                  wenn zu lang
	 */
	public void checkTooLongAndThrow(final String name, final int minimalFontSize) {

		final float breite = new FontCalulator().berechneBreite(name, minimalFontSize);

		if (breite > UrkundePDFUtils.MAX_WIDTH * 2) {

			throw new IllegalArgumentException("name passt nicht in drei Zeilen");
		}
	}

	/**
	 * Stellt fest, ob der gegebene name umgebrochen werden muss.
	 *
	 * @param  name
	 *                                  String
	 * @param  fontSizes
	 *                                  Integer[] die zu testenden FontSizes.
	 * @return                          boolean
	 * @throws IllegalArgumentException
	 *                                  wenn zu lang
	 */
	public boolean needsWrapping(final String name, final Integer[] fontSizes) {

		new LengthTester().checkTooLongAndThrow(name, fontSizes[fontSizes.length - 1]);

		for (final int size : fontSizes) {

			final float breite = new FontCalulator().berechneBreite(name, size);
			final float difference = UrkundePDFUtils.MAX_WIDTH - breite;
			LOG.debug("{}, breite={}, differenz={}", name, breite, difference);

			if (breite <= UrkundePDFUtils.MAX_WIDTH) {

				return false;
			}
		}
		return true;
	}

}
