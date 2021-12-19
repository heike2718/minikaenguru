// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.mail.WartezeitUtil;

/**
 * WartezeitUtilTest
 */
public class WartezeitUtilTest {

	@Test
	void should_wartezeit_beBetweenMinAndMax() {

		// Arrange
		int errorCount = 0;
		int minVal = 10;
		int maxVal = 23;

		// Act
		for (int i = 0; i < 5000; i++) {

			int wartezeit = WartezeitUtil.getWartezeit(minVal, maxVal);
			// System.out.println("Wartezeit=" + wartezeit);

			if (wartezeit < minVal) {

				errorCount++;
			}

			if (wartezeit > maxVal) {

				errorCount++;
			}
		}

		// Assert
		assertEquals(0, errorCount);

	}

}
