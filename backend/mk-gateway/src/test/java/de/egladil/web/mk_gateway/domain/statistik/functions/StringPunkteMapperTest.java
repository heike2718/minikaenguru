// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * StringPunkteMapperTest
 */
public class StringPunkteMapperTest {

	@Test
	void should_returnNull_when_null() {

		// Arrange
		String punkte = null;
		Integer expected = null;

		// Act + Assert
		assertEquals(expected, new StringPunkteMapper().apply(punkte));

	}

	@Test
	void should_returnNull_when_blank() {

		// Arrange
		String punkte = "  ";
		Integer expected = null;

		// Act + Assert
		assertEquals(expected, new StringPunkteMapper().apply(punkte));

	}

	@Test
	void should_returnNull_when_keineZahl() {

		// Arrange
		String punkte = "hallo";
		Integer expected = null;

		// Act + Assert
		assertEquals(expected, new StringPunkteMapper().apply(punkte));

	}

	@Test
	void should_returnNull_when_fuenfstellig() {

		// Arrange
		String punkte = "68,725";
		Integer expected = null;

		// Act + Assert
		assertEquals(expected, new StringPunkteMapper().apply(punkte));

	}

	@Test
	void should_mapCorrectly_when_viertstellig() {

		// Arrange
		String punkte = "68,25";
		Integer expected = 6825;

		// Act + Assert
		assertEquals(expected, new StringPunkteMapper().apply(punkte));

	}

	@Test
	void should_mapCorrectly_when_dreistelligUndZweiVorkommastellen() {

		// Arrange
		String punkte = "68,5";
		Integer expected = 6850;

		// Act + Assert
		assertEquals(expected, new StringPunkteMapper().apply(punkte));

	}

	@Test
	void should_mapCorrectly_when_dreistelligUndEineVorkommastelle() {

		// Arrange
		String punkte = "8,75";
		Integer expected = 875;

		// Act + Assert
		assertEquals(expected, new StringPunkteMapper().apply(punkte));

	}

	@Test
	void should_mapCorrectly_when_zweistelligUndNullVorkommastellen() {

		// Arrange
		String punkte = "75";
		Integer expected = 7500;

		// Act + Assert
		assertEquals(expected, new StringPunkteMapper().apply(punkte));

	}

	@Test
	void should_mapCorrectly_when_zweistelligUndEineVorkommastelle() {

		// Arrange
		String punkte = "7,5";
		Integer expected = 750;

		// Act + Assert
		assertEquals(expected, new StringPunkteMapper().apply(punkte));

	}

	@Test
	void should_mapCorrectly_when_einstellig() {

		// Arrange
		String punkte = "5";
		Integer expected = 500;

		// Act + Assert
		assertEquals(expected, new StringPunkteMapper().apply(punkte));

	}

}
