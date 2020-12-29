// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import java.util.List;

/**
 * LineSplitter
 */
public class LineSplitter {

	private final Integer[] orderedFontSizes;

	private final LengthTester lengthTester;

	public LineSplitter(final Integer[] orderedFontSizes) {

		this.orderedFontSizes = orderedFontSizes;
		this.lengthTester = new LengthTester();
	}

	public List<String> split() {

		return null;
	}

}
