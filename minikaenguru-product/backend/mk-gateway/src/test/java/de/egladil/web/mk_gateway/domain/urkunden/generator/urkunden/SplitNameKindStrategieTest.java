// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.FontCalulator;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.FontSizeAndLines;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.SplitNameKindStrategie;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.UrkundePDFUtils;

/**
 * SplitNameKindStrategieTest
 */
public class SplitNameKindStrategieTest {

	private FontCalulator fontCalculator = new FontCalulator();

	private SplitNameKindStrategie strategie = new SplitNameKindStrategie();

	@Test
	void should_berechnen_when_nurEinEinzeiler() {

		// Arrange
		String text = "Karl Theodor zu Guttenberg Kuckucksheim";

		strategie = new SplitNameKindStrategie();
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(4, result.size());

		{

			FontSizeAndLines fsl = result.get(0);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(14, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Karl Theodor zu Guttenberg Kuckucksheim", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		{

			FontSizeAndLines fsl = result.get(1);
			assertEquals(16, fsl.fontSize().intValue());
			assertEquals(0, fsl.anzahlZeilen());

		}

		{

			FontSizeAndLines fsl = result.get(2);
			assertEquals(18, fsl.fontSize().intValue());
			assertEquals(0, fsl.anzahlZeilen());

		}

		{

			FontSizeAndLines fsl = result.get(3);
			assertEquals(20, fsl.fontSize().intValue());
			assertEquals(0, fsl.anzahlZeilen());
		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();

		assertEquals(14, fontSize);

	}

	@Test
	void should_berechnen_when_allesEinzeiler() {

		// Arrange
		String text = "Karl Theodor zu Guttenberg";

		// Act
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(4, result.size());

		{

			FontSizeAndLines fsl = result.get(0);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(14, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Karl Theodor zu Guttenberg", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		{

			FontSizeAndLines fsl = result.get(1);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(16, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Karl Theodor zu Guttenberg", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		{

			FontSizeAndLines fsl = result.get(2);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(18, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Karl Theodor zu Guttenberg", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}

		}

		{

			FontSizeAndLines fsl = result.get(3);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(20, fontSize);
			assertEquals(1, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Karl Theodor zu Guttenberg", lines.get(0));

			{

				float breite = fontCalculator.berechneBreite(lines.get(0), fontSize);
				assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);
			}
		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();

		assertEquals(20, fontSize);

	}

	@Test
	void should_berechnen_when_Zweizeiler() {

		// Arrange
		String text = "Karl mit einem Nachnamen der gerade noch so passt wenn umgebrochen wird";

		strategie = new SplitNameKindStrategie();
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(4, result.size());

		{

			FontSizeAndLines fsl = result.get(0);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(14, fontSize);
			assertEquals(2, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Karl mit einem Nachnamen der gerade noch", lines.get(0));
			assertEquals("so passt wenn umgebrochen wird", lines.get(1));

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
			assertEquals(16, fontSize);
			assertEquals(2, fsl.anzahlZeilen());

			Optional<List<String>> optLines = fsl.lines();
			assertTrue(optLines.isPresent());

			List<String> lines = optLines.get();
			assertEquals("Karl mit einem Nachnamen der gerade", lines.get(0));
			assertEquals("noch so passt wenn umgebrochen wird", lines.get(1));

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

			FontSizeAndLines fsl = result.get(2);
			assertEquals(18, fsl.fontSize().intValue());
			assertEquals(0, fsl.anzahlZeilen());

		}

		{

			FontSizeAndLines fsl = result.get(3);
			assertEquals(20, fsl.fontSize().intValue());
			assertEquals(0, fsl.anzahlZeilen());
		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();

		assertEquals(16, fontSize);

	}

	@Test
	void should_berechnen_when_zuLang() {

		// Arrange
		String text = "Maximilian Konstantin dessen Nachname beim besten Willen nicht nicht auf die Urkunde passen wird";

		strategie = new SplitNameKindStrategie();
		List<FontSizeAndLines> result = strategie.split(text);

		// Assert
		assertEquals(4, result.size());

		{

			FontSizeAndLines fsl = result.get(0);
			int fontSize = fsl.fontSize().intValue();
			assertEquals(14, fontSize);
			assertEquals(0, fsl.anzahlZeilen());

		}

		{

			FontSizeAndLines fsl = result.get(1);
			assertEquals(16, fsl.fontSize().intValue());
			assertEquals(0, fsl.anzahlZeilen());

		}

		{

			FontSizeAndLines fsl = result.get(2);
			assertEquals(18, fsl.fontSize().intValue());
			assertEquals(0, fsl.anzahlZeilen());

		}

		{

			FontSizeAndLines fsl = result.get(3);
			assertEquals(20, fsl.fontSize().intValue());
			assertEquals(0, fsl.anzahlZeilen());
		}

		// Act
		FontSizeAndLines fontSizeAndLines = strategie.getFontSizeAndLines(text);

		int fontSize = fontSizeAndLines.fontSize().intValue();
		assertEquals(20, fontSize);

		String abbreviatedText = fontSizeAndLines.lines().get().get(0);
		float breite = fontCalculator.berechneBreite(abbreviatedText, fontSize);
		assertTrue(breite < UrkundePDFUtils.MAX_WIDTH);

		assertEquals("Maximilian Konstantin dessen ...", abbreviatedText);

	}

}
