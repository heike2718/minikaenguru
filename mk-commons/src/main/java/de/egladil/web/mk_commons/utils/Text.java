// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Text
 */
public class Text {

	private List<String> text;

	private List<String> transformedText;

	/**
	 * Text
	 *
	 * @param List
	 */
	public Text(final List<String> text) {

		if (text == null) {

			throw new IllegalArgumentException("text null");
		}
		init(text);
	}

	/**
	 * Text
	 *
	 * @param String[]
	 */
	public Text(final String[] text) {

		if (text == null) {

			throw new IllegalArgumentException("text null");
		}
		init(Arrays.asList(text));
	}

	/**
	 * @param text
	 */
	private void init(final List<String> text) {

		this.text = text;
		this.transformedText = new ArrayList<>();
		transformedText.addAll(text);
	}

	public final List<String> getTransformedText() {

		return transformedText;
	}

	public final void setTransformedText(final List<String> transformedText) {

		this.transformedText = transformedText;
	}

	public final List<String> getText() {

		return text;
	}

}
