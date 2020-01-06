// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * StringListLinerTest
 */
public class StringListLinerTest {

	@Test
	void should_handle_null_correctly() {

		// Act
		String result = new StringListLiner().apply(null);

		// Assert
		assertNull(result);
	}

	@Test
	void should_handle_empty_correctly() {

		// Act
		String result = new StringListLiner().apply(Arrays.asList(new String[0]));

		// Assert
		assertEquals("", result);
	}

	@Test
	void should_handle_ComplexStrings_correctly() {

		// Arrange
		List<String> input = Arrays
			.asList(new String[] { "  Das ", " ist  ", "ein Text  ", "mit   vielen  ", "unnötigen Leerzeichen.  " });

		// Act
		String result = new StringListLiner().apply(input);

		// Assert
		assertEquals("Das ist ein Text mit vielen unnötigen Leerzeichen.", result);
	}

	@Test
	void should_handle_ListWithNull_correctly() {

		// Arrange
		List<String> input = Arrays
			.asList(new String[] { "  Das ", " ist  ", null, "ein Text  ", "mit   vielen  ", "unnötigen Leerzeichen.  " });

		// Act
		String result = new StringListLiner().apply(input);

		// Assert
		assertEquals("Das ist ein Text mit vielen unnötigen Leerzeichen.", result);
	}
}
