// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;

/**
 * FontSizeAndLines
 */
@ValueObject
public class FontSizeAndLines {

	private final Integer fontSize;

	private final List<String> lines;

	public FontSizeAndLines(final Integer fontSize) {

		this.fontSize = fontSize;
		this.lines = null;
	}

	/**
	 * @param fontSize
	 * @param lines
	 *                 List darf null sein, falls der gesamte Text zu lang für die fontSize ist.
	 */
	public FontSizeAndLines(final Integer fontSize, final List<String> lines) {

		this.fontSize = fontSize;
		this.lines = lines;
	}

	public Integer fontSize() {

		return fontSize;
	}

	/**
	 * @return
	 */
	public Optional<List<String>> lines() {

		return lines == null ? Optional.empty() : Optional.of(lines);
	}

	public int anzahlZeilen() {

		return lines == null ? 0 : lines.size();
	}

	@Override
	public String toString() {

		return "FontSizeAndLines [fontSize=" + fontSize + ", lines=" + lines + "]";
	}
}
