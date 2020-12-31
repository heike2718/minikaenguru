// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * AbstractSplitTextStrategie
 */
public abstract class AbstractSplitTextStrategie implements SplitTextStrategy {

	private FontCalulator fontCalulator = new FontCalulator();

	/**
	 * Bricht den gegebenen text um oder auch nicht.
	 *
	 * @param  text
	 *              String
	 * @return      List die Zeilen mit der zugehörigen FontSize
	 */
	protected List<FontSizeAndLines> split(final String text) {

		List<FontSizeAndLines> result = new ArrayList<>();
		List<Integer> fontSizes = getFontSizes();

		for (Integer fontSize : fontSizes) {

			result.add(splitWithFontSize(fontSize, text));
		}

		return result;
	}

	@Override
	public FontSizeAndLines getFontSizeAndLines(final String text) {

		List<FontSizeAndLines> fontSizesAndLines = split(text);
		Collections.sort(fontSizesAndLines, new FontSizeLinesDescendingComparator());

		for (FontSizeAndLines fsl : fontSizesAndLines) {

			if (fsl.anzahlZeilen() > 0) {

				return fsl;
			}
		}

		return new FontSizeAndLines(getMaxFontSizeAbbreviatedText(),
			Arrays.asList(new String[] { StringUtils.abbreviate(text, getMaxLengthAbbreviatedText()) }));
	}

	/**
	 * @return
	 */
	protected abstract List<Integer> getFontSizes();

	/**
	 * Gibt die maximale Länge des Textes zurück, der gedruckt wird, wenn Umbruch nicht mehr sinnvoll ist.
	 *
	 * @return
	 */
	protected abstract int getMaxLengthAbbreviatedText();

	private FontSizeAndLines splitWithFontSize(final int fontSize, final String text) {

		List<String> zeilen = getLines(text, fontSize);

		if (zeilen.size() == 1) {

			float breite = fontCalulator.berechneBreite(text, fontSize);

			if (breite > UrkundePDFUtils.MAX_WIDTH) {

				return new FontSizeAndLines(fontSize);
			} else {

				return new FontSizeAndLines(fontSize, zeilen);
			}
		}

		float breiteZeile2 = fontCalulator.berechneBreite(zeilen.get(1), fontSize);

		if (breiteZeile2 <= UrkundePDFUtils.MAX_WIDTH) {

			return new FontSizeAndLines(fontSize, zeilen);
		}

		List<String> neueZeilen = new ArrayList<>();
		neueZeilen.add(zeilen.get(0));

		zeilen = getLines(zeilen.get(1), fontSize);
		neueZeilen.addAll(zeilen);

		return new FontSizeAndLines(fontSize, neueZeilen);
	}

	private List<String> getLines(final String text, final int fontSize) {

		String[] worte = StringUtils.split(text);

		if (worte.length <= 1) {

			return Arrays.asList(new String[] { text });
		}

		int anzahlWorte = 1;
		String[] worte1 = new String[anzahlWorte];

		for (int i = 0; i < anzahlWorte; i++) {

			worte1[i] = worte[i];
		}
		String text1 = StringUtils.join(worte1, " ");
		float breite = fontCalulator.berechneBreite(text1, fontSize);

		while (anzahlWorte < worte.length) {

			if (breite > UrkundePDFUtils.MAX_WIDTH) {

				anzahlWorte--;
				worte1 = new String[anzahlWorte];

				for (int i = 0; i < anzahlWorte; i++) {

					worte1[i] = worte[i];
				}

				text1 = StringUtils.join(worte1, " ");
				breite = fontCalulator.berechneBreite(text1, fontSize);
				break;
			}

			anzahlWorte++;

			worte1 = new String[anzahlWorte];

			for (int i = 0; i < anzahlWorte; i++) {

				worte1[i] = worte[i];
			}

			text1 = StringUtils.join(worte1, " ");
			breite = fontCalulator.berechneBreite(text1, fontSize);
		}

		if (anzahlWorte == worte.length && breite > UrkundePDFUtils.MAX_WIDTH) {

			return Arrays.asList(new String[] { text });

		}
		String[] worte2 = new String[worte.length - anzahlWorte];

		int min = anzahlWorte;

		for (int i = min; i < worte.length; i++) {

			int j = i - min;
			worte2[j] = worte[i];

		}

		String text2 = StringUtils.join(worte2, " ");

		breite = fontCalulator.berechneBreite(text2, fontSize);

		if (breite > UrkundePDFUtils.MAX_WIDTH) {

			return Arrays.asList(new String[] { text });
		}

		return text2.length() > 0 ? Arrays.asList(new String[] { text1, text2 }) : Arrays.asList(new String[] { text1 });
	}

}
