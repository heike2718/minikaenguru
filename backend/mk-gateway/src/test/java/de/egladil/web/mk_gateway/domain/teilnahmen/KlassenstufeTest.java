// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * KlassenstufeTest
 */
public class KlassenstufeTest {

	@Test
	void should_getAufgabennummernWithWertungscodeIndexReturnCorrectValues_when_Jahr2018() {

		// Arrange
		Integer jahr = 2018;

		// Act
		{

			Map<String, Integer> result = Klassenstufe.IKID.getAufgabennummernWithWertungscodeIndex(jahr);
			assertEquals(6, result.size());

			for (String key : result.keySet()) {

				Integer value = result.get(key);

				if ("A-1".equals(key)) {

					assertEquals(Integer.valueOf(0), value);
				}

				if ("A-2".equals(key)) {

					assertEquals(Integer.valueOf(1), value);
				}

				if ("B-1".equals(key)) {

					assertEquals(Integer.valueOf(2), value);
				}

				if ("B-2".equals(key)) {

					assertEquals(Integer.valueOf(3), value);
				}

				if ("C-1".equals(key)) {

					assertEquals(Integer.valueOf(4), value);
				}

				if ("C-2".equals(key)) {

					assertEquals(Integer.valueOf(5), value);
				}

			}
		}

		{

			Map<String, Integer> result = Klassenstufe.EINS.getAufgabennummernWithWertungscodeIndex(jahr);
			assertEquals(12, result.size());

			for (String key : result.keySet()) {

				Integer value = result.get(key);

				if ("A-1".equals(key)) {

					assertEquals(Integer.valueOf(0), value);
				}

				if ("A-2".equals(key)) {

					assertEquals(Integer.valueOf(1), value);
				}

				if ("A-3".equals(key)) {

					assertEquals(Integer.valueOf(2), value);
				}

				if ("A-4".equals(key)) {

					assertEquals(Integer.valueOf(3), value);
				}

				if ("B-1".equals(key)) {

					assertEquals(Integer.valueOf(4), value);
				}

				if ("B-2".equals(key)) {

					assertEquals(Integer.valueOf(5), value);
				}

				if ("B-3".equals(key)) {

					assertEquals(Integer.valueOf(6), value);
				}

				if ("B-4".equals(key)) {

					assertEquals(Integer.valueOf(7), value);
				}

				if ("C-1".equals(key)) {

					assertEquals(Integer.valueOf(8), value);
				}

				if ("C-2".equals(key)) {

					assertEquals(Integer.valueOf(9), value);
				}

				if ("C-3".equals(key)) {

					assertEquals(Integer.valueOf(10), value);
				}

				if ("C-4".equals(key)) {

					assertEquals(Integer.valueOf(11), value);
				}
			}
		}

		{

			Map<String, Integer> result = Klassenstufe.ZWEI.getAufgabennummernWithWertungscodeIndex(jahr);
			assertEquals(15, result.size());

			for (String key : result.keySet()) {

				Integer value = result.get(key);

				if ("A-1".equals(key)) {

					assertEquals(Integer.valueOf(0), value);
				}

				if ("A-2".equals(key)) {

					assertEquals(Integer.valueOf(1), value);
				}

				if ("A-3".equals(key)) {

					assertEquals(Integer.valueOf(2), value);
				}

				if ("A-4".equals(key)) {

					assertEquals(Integer.valueOf(3), value);
				}

				if ("A-5".equals(key)) {

					assertEquals(Integer.valueOf(4), value);
				}

				if ("B-1".equals(key)) {

					assertEquals(Integer.valueOf(5), value);
				}

				if ("B-2".equals(key)) {

					assertEquals(Integer.valueOf(6), value);
				}

				if ("B-3".equals(key)) {

					assertEquals(Integer.valueOf(7), value);
				}

				if ("B-4".equals(key)) {

					assertEquals(Integer.valueOf(8), value);
				}

				if ("B-5".equals(key)) {

					assertEquals(Integer.valueOf(9), value);
				}

				if ("C-1".equals(key)) {

					assertEquals(Integer.valueOf(10), value);
				}

				if ("C-2".equals(key)) {

					assertEquals(Integer.valueOf(11), value);
				}

				if ("C-3".equals(key)) {

					assertEquals(Integer.valueOf(12), value);
				}

				if ("C-4".equals(key)) {

					assertEquals(Integer.valueOf(13), value);
				}

				if ("C-5".equals(key)) {

					assertEquals(Integer.valueOf(14), value);
				}

			}
		}
	}

	@Test
	void should_getAufgabennummernWithWertungscodeIndexReturnCorrectValues_when_JahrBetween2014And2016() {

		// Arrange
		Integer jahr = 2014 + new Random().nextInt(3);

		// Act
		{

			assertTrue(Klassenstufe.IKID.getAufgabennummernWithWertungscodeIndex(jahr).isEmpty());
		}

		{

			Map<String, Integer> result = Klassenstufe.EINS.getAufgabennummernWithWertungscodeIndex(jahr);
			assertEquals(15, result.size());

			for (String key : result.keySet()) {

				Integer value = result.get(key);

				if ("A-1".equals(key)) {

					assertEquals(Integer.valueOf(0), value);
				}

				if ("A-2".equals(key)) {

					assertEquals(Integer.valueOf(1), value);
				}

				if ("A-3".equals(key)) {

					assertEquals(Integer.valueOf(2), value);
				}

				if ("A-4".equals(key)) {

					assertEquals(Integer.valueOf(3), value);
				}

				if ("A-5".equals(key)) {

					assertEquals(Integer.valueOf(4), value);
				}

				if ("B-1".equals(key)) {

					assertEquals(Integer.valueOf(5), value);
				}

				if ("B-2".equals(key)) {

					assertEquals(Integer.valueOf(6), value);
				}

				if ("B-3".equals(key)) {

					assertEquals(Integer.valueOf(7), value);
				}

				if ("B-4".equals(key)) {

					assertEquals(Integer.valueOf(8), value);
				}

				if ("B-5".equals(key)) {

					assertEquals(Integer.valueOf(9), value);
				}

				if ("C-1".equals(key)) {

					assertEquals(Integer.valueOf(10), value);
				}

				if ("C-2".equals(key)) {

					assertEquals(Integer.valueOf(11), value);
				}

				if ("C-3".equals(key)) {

					assertEquals(Integer.valueOf(12), value);
				}

				if ("C-4".equals(key)) {

					assertEquals(Integer.valueOf(13), value);
				}

				if ("C-5".equals(key)) {

					assertEquals(Integer.valueOf(14), value);
				}

			}
		}

		{

			Map<String, Integer> result = Klassenstufe.ZWEI.getAufgabennummernWithWertungscodeIndex(jahr);
			assertEquals(15, result.size());

			for (String key : result.keySet()) {

				Integer value = result.get(key);

				if ("A-1".equals(key)) {

					assertEquals(Integer.valueOf(0), value);
				}

				if ("A-2".equals(key)) {

					assertEquals(Integer.valueOf(1), value);
				}

				if ("A-3".equals(key)) {

					assertEquals(Integer.valueOf(2), value);
				}

				if ("A-4".equals(key)) {

					assertEquals(Integer.valueOf(3), value);
				}

				if ("A-5".equals(key)) {

					assertEquals(Integer.valueOf(4), value);
				}

				if ("B-1".equals(key)) {

					assertEquals(Integer.valueOf(5), value);
				}

				if ("B-2".equals(key)) {

					assertEquals(Integer.valueOf(6), value);
				}

				if ("B-3".equals(key)) {

					assertEquals(Integer.valueOf(7), value);
				}

				if ("B-4".equals(key)) {

					assertEquals(Integer.valueOf(8), value);
				}

				if ("B-5".equals(key)) {

					assertEquals(Integer.valueOf(9), value);
				}

				if ("C-1".equals(key)) {

					assertEquals(Integer.valueOf(10), value);
				}

				if ("C-2".equals(key)) {

					assertEquals(Integer.valueOf(11), value);
				}

				if ("C-3".equals(key)) {

					assertEquals(Integer.valueOf(12), value);
				}

				if ("C-4".equals(key)) {

					assertEquals(Integer.valueOf(13), value);
				}

				if ("C-5".equals(key)) {

					assertEquals(Integer.valueOf(14), value);
				}

			}
		}
	}

	@Test
	void should_getAufgabennummernWithWertungscodeIndexReturnCorrectValues_when_JahrBetween2010And2013() {

		// Arrange
		Integer jahr = 2010 + new Random().nextInt(4);

		// Act
		{

			assertTrue(Klassenstufe.IKID.getAufgabennummernWithWertungscodeIndex(jahr).isEmpty());
		}

		{

			assertTrue(Klassenstufe.EINS.getAufgabennummernWithWertungscodeIndex(jahr).isEmpty());
		}

		{

			Map<String, Integer> result = Klassenstufe.ZWEI.getAufgabennummernWithWertungscodeIndex(jahr);
			assertEquals(15, result.size());

			for (String key : result.keySet()) {

				Integer value = result.get(key);

				if ("A-1".equals(key)) {

					assertEquals(Integer.valueOf(0), value);
				}

				if ("A-2".equals(key)) {

					assertEquals(Integer.valueOf(1), value);
				}

				if ("A-3".equals(key)) {

					assertEquals(Integer.valueOf(2), value);
				}

				if ("A-4".equals(key)) {

					assertEquals(Integer.valueOf(3), value);
				}

				if ("A-5".equals(key)) {

					assertEquals(Integer.valueOf(4), value);
				}

				if ("B-1".equals(key)) {

					assertEquals(Integer.valueOf(5), value);
				}

				if ("B-2".equals(key)) {

					assertEquals(Integer.valueOf(6), value);
				}

				if ("B-3".equals(key)) {

					assertEquals(Integer.valueOf(7), value);
				}

				if ("B-4".equals(key)) {

					assertEquals(Integer.valueOf(8), value);
				}

				if ("B-5".equals(key)) {

					assertEquals(Integer.valueOf(9), value);
				}

				if ("C-1".equals(key)) {

					assertEquals(Integer.valueOf(10), value);
				}

				if ("C-2".equals(key)) {

					assertEquals(Integer.valueOf(11), value);
				}

				if ("C-3".equals(key)) {

					assertEquals(Integer.valueOf(12), value);
				}

				if ("C-4".equals(key)) {

					assertEquals(Integer.valueOf(13), value);
				}

				if ("C-5".equals(key)) {

					assertEquals(Integer.valueOf(14), value);
				}
			}
		}
	}
}
