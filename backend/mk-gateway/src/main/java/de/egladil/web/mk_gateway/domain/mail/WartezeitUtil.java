// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.Random;

/**
 * WartezeitUtil
 */
public class WartezeitUtil {

	public static int getWartezeit(final int minVal, final int maxVal) {

		Integer integer = new Random().nextInt(maxVal);

		int result = integer.intValue();

		if (result < minVal) {

			result += minVal;
		}

		if (result > maxVal) {

			return maxVal;
		}

		return result;

	}
}
