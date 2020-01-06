// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * TrimShrinkerTest
 */
public class TrimShrinkerTest {

	@Test
	void should_handle_null_correctly() {

		// Act
		String result = new TrimShrinker().apply(null);

		// Assert
		assertNull(result);
	}

	@Test
	void should_handle_empty_correctly() {

		// Act
		String result = new TrimShrinker().apply("");

		// Assert
		assertEquals("", result);
	}

	@Test
	void should_handle_blank_correctly() {

		// Arrange
		StringBuilder sb = new StringBuilder();
		int max = new Random().nextInt(100) + 3;

		for (int i = 0; i < max; i++) {

			sb.append(" ");
		}

		String input = sb.toString();

		// Act
		String result = new TrimShrinker().apply(input);

		// Assert
		assertEquals("", result);
	}

	@Test
	void should_handle_ComplexString_correctly() {

		// Arrange
		String input = "  Das  ist ein Text    mit   vielen    unnötigen Leerzeichen.   ";

		// Act
		String result = new TrimShrinker().apply(input);

		// Assert
		assertEquals("Das ist ein Text mit vielen unnötigen Leerzeichen.", result);
	}
}
