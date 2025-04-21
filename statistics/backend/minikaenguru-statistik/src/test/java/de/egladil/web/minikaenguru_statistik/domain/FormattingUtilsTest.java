// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * FormattingUtilsTest
 */
public class FormattingUtilsTest {

	@Test
	void testFormat() {

		// Arrange
		double value = 62.45;
		String expected = "62,45";

		// Act
		String actual = FormattingUtils.doubleAsString(value);

		// Assert
		assertEquals(expected, actual);

	}
}
