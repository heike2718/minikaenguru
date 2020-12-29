// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import java.util.Arrays;
import java.util.List;

/**
 * SplitNameKindStrategie
 */
public class SplitNameKindStrategie extends AbstractSplitTextStrategie {

	@Override
	protected List<Integer> getFontSizes() {

		List<Integer> fontSizes = Arrays
			.asList(
				new Integer[] { UrkundePDFUtils.SIZE_TEXT_NORMAL, UrkundePDFUtils.SIZE_NAME_SMALL, UrkundePDFUtils.SIZE_NAME_MEDIUM,
					UrkundePDFUtils.SIZE_NAME_LARGE });
		return fontSizes;
	}

	@Override
	protected int getMaxLengthAbbreviatedText() {

		return 32;
	}

	@Override
	protected int getMaxFontSizeAbbreviatedText() {

		return UrkundePDFUtils.SIZE_NAME_LARGE;
	}
}
