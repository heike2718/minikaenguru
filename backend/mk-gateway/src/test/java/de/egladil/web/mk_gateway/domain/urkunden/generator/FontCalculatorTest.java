// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * FontCalculatorTest
 */
public class FontCalculatorTest {

	private FontCalulator fontCalculator = new FontCalulator();

	@Test
	void should_berechneBreite_when_schulnameMaxEinzeilig() {

		// Arrange
		List<Integer> fontSizes = Arrays.asList(new Integer[] { 12, 14 });

		String schulname = "Grundschule \"Johann Wolfgang von Goethe\"";

		// Act
		for (Integer fontSize : fontSizes) {

			float breite = fontCalculator.berechneBreite(schulname, fontSize);

			System.out.println(schulname + ": fontSize=" + fontSize + ", breite=" + breite);
		}

	}

	@Test
	void should_berechneBreite_when_schulnameMaxZweizeilig() {

		// Arrange
		List<Integer> fontSizes = Arrays.asList(new Integer[] { 12, 14 });

		String schulname = "Grundschule an der Georgstraße Städtische Gemeinschaftsschule der Primarstufe";

		// Act
		for (Integer fontSize : fontSizes) {

			float breite = fontCalculator.berechneBreite(schulname, fontSize);

			System.out.println(schulname + ": fontSize=" + fontSize + ", breite=" + breite);
		}

	}
}
