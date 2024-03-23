// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

/**
 * SplitTextStrategy
 */
public interface SplitTextStrategy {

	/**
	 * Sucht den größtmöglichen Font und die zugehörigen Zeilen. Wenn ein Umbruch mehr als 2 Zeilen erfordern würde, wir auf eine
	 * passende Länge abgekürzt und der größte Font gewählt, so dass der abgekürzte Text in eine Zeile mit der bevorzugten fontSize
	 * dargestellt wird.
	 *
	 * @param  text
	 *              String der text
	 * @return      FontSizeAndLines hat immer einen text, schlimmstenfalls einen abgekürzten.
	 */
	FontSizeAndLines getFontSizeAndLines(final String text);

	/**
	 * Maximale fonzSize des abgekürzten Textes.
	 *
	 * @return
	 */
	int getMaxFontSizeAbbreviatedText();

}
