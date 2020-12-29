// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * SplitSchulnameStrategieTest
 */
public class SplitSchulnameStrategieTest {

	private static final int MAX_FONT_SIZE = 14;

	private static final int MAX_LENGTH_ABBREVIATED_TEXT = 35;

	private FontCalulator fontCalculator = new FontCalulator();

	private SplitSchulnameStrategie strategie = new SplitSchulnameStrategie();

	@Test
	void should_berechnen_when_achtWorte() {

		// Arrange
		String text = "Grundschule an der Georgstraße Städtische Gemeinschaftsschule der Primarstufe";

		// Act
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(2, result.size());

		{

			FontSizeAndLines fsl = result.get(0);

			int fontSize = fsl.fontSize().intValue();
			assertEquals(12, fontSize);
			assertEquals(2, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Grundschule an der Georgstraße Städtische", lines.get(0));
			assertEquals("Gemeinschaftsschule der Primarstufe", lines.get(1));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

			{

				float breite = fontCalculator.berechneBreite(lines.get(1), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		{

			FontSizeAndLines fsl = result.get(1);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(14, fontSize);
			assertEquals(2, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Grundschule an der Georgstraße Städtische", lines.get(0));
			assertEquals("Gemeinschaftsschule der Primarstufe", lines.get(1));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

			{

				float breite = fontCalculator.berechneBreite(lines.get(1), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();

		assertEquals(MAX_FONT_SIZE, fontSize);

	}

	@Test
	void should_berechnen_when_siebenWorte() {

		// Arrange
		String text = "Lichtenbergschule Grund- und Werkrealschule Außenstelle Grundschule Gronau";

		// Act
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(2, result.size());

		{

			FontSizeAndLines fsl = result.get(0);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(12, fontSize);
			assertEquals(2, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Lichtenbergschule Grund- und Werkrealschule", lines.get(0));
			assertEquals("Außenstelle Grundschule Gronau", lines.get(1));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

			{

				float breite = fontCalculator.berechneBreite(lines.get(1), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		{

			FontSizeAndLines fsl = result.get(1);
			assertEquals(14, fsl.fontSize().intValue());
			assertEquals(0, fsl.anzahlZeilen());
		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();

		assertEquals(12, fontSize);

	}

	@Test
	void should_berechnen_when_mittlereLaenge() {

		// Arrange
		String text = "Brüder-Grimm-Schule Grundschule der Stadt Leipzig";

		// Act
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(2, result.size());

		{

			FontSizeAndLines fsl = result.get(0);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(12, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Brüder-Grimm-Schule Grundschule der Stadt Leipzig", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		{

			FontSizeAndLines fsl = result.get(1);
			assertEquals(14, fsl.fontSize().intValue());
			assertEquals(0, fsl.anzahlZeilen());
		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();

		assertEquals(12, fontSize);

	}

	@Test
	void should_berechnen_when_einWort() {

		// Arrange
		String text = "Uhlandschule";

		// Act
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(2, result.size());

		{

			FontSizeAndLines fsl = result.get(0);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(12, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Uhlandschule", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		{

			FontSizeAndLines fsl = result.get(1);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(14, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Uhlandschule", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();

		assertEquals(MAX_FONT_SIZE, fontSize);

	}

	@Test
	void should_berechnen_when_leer() {

		// Arrange
		String text = "";

		// Act
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(2, result.size());

		{

			FontSizeAndLines fsl = result.get(0);
			assertEquals(12, fsl.fontSize().intValue());
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("", lines.get(0));

		}

		{

			FontSizeAndLines fsl = result.get(1);
			assertEquals(14, fsl.fontSize().intValue());
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("", lines.get(0));
		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();

		assertEquals(MAX_FONT_SIZE, fontSize);

	}

	@Test
	void should_berechnen_when_zweiWorte() {

		// Arrange
		String text = "Antoniusschule Gemeinschaftsgrundschule";

		// Act
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(2, result.size());

		{

			FontSizeAndLines fsl = result.get(0);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(12, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Antoniusschule Gemeinschaftsgrundschule", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		{

			FontSizeAndLines fsl = result.get(1);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(14, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Antoniusschule Gemeinschaftsgrundschule", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}
		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();

		assertEquals(MAX_FONT_SIZE, fontSize);

	}

	@Test
	void should_berechnen_when_fuenfWorte() {

		// Arrange
		String text = "Grundschule \"Johann Wolfgang von Goethe\"";

		// Act
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(2, result.size());

		{

			FontSizeAndLines fsl = result.get(0);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(12, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Grundschule \"Johann Wolfgang von Goethe\"", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		{

			FontSizeAndLines fsl = result.get(1);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(14, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Grundschule \"Johann Wolfgang von Goethe\"", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}
		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();

		assertEquals(MAX_FONT_SIZE, fontSize);

	}

	@Test
	void should_berechnen_when_laengsterSchulname() {

		// Arrange
		String text = "St.-Stephanus-Schule Städt. Kath. Grundschule Leverkusen-Hitdorf - Primarstufe -";

		// Act
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(2, result.size());

		{

			FontSizeAndLines fsl = result.get(0);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(12, fontSize);
			assertEquals(2, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("St.-Stephanus-Schule Städt. Kath. Grundschule", lines.get(0));
			assertEquals("Leverkusen-Hitdorf - Primarstufe -", lines.get(1));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

			{

				float breite = fontCalculator.berechneBreite(lines.get(1), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		{

			FontSizeAndLines fsl = result.get(1);
			assertEquals(14, fsl.fontSize().intValue());
			assertEquals(0, fsl.anzahlZeilen());
		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();

		assertEquals(12, fontSize);

	}

}
