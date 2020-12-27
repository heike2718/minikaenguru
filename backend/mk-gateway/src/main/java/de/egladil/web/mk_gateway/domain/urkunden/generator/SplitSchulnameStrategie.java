// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import java.util.Arrays;
import java.util.List;

/**
 * SplitSchulnameStrategie
 */
public class SplitSchulnameStrategie extends AbstractSplitTextStrategie {

	@Override
	protected List<Integer> getFontSizes() {

		List<Integer> fontSizes = Arrays
			.asList(new Integer[] { UrkundePDFUtils.SIZE_TEXT_SMALL, UrkundePDFUtils.SIZE_TEXT_NORMAL });
		return fontSizes;
	}

	@Override
	protected int getMaxLengthAbbreviatedText() {

		return 45;
	}

	@Override
	protected int getMaxFontSizeAbbreviatedText() {

		return UrkundePDFUtils.SIZE_TEXT_NORMAL;
	}

}
