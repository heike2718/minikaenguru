// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * PunkteStringMapperTest
 */
public class PunkteStringMapperTest {

	@Test
	void should_splitWithKomma() {

		// Arrange
		Integer punkte = Integer.valueOf(2575);
		String expected = "25,75";

		// Act + Assert
		assertEquals(expected, new PunkteStringMapper().apply(punkte));

	}

	@Test
	void should_appendZeroAndsplitWithKomma() {

		// Arrange
		Integer punkte = Integer.valueOf(870);
		String expected = "8,70";

		// Act + Assert
		assertEquals(expected, new PunkteStringMapper().apply(punkte));

	}
}
