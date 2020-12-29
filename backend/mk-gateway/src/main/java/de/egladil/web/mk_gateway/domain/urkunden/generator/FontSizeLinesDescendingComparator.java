// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import java.util.Comparator;

/**
 * FontSizeLinesDescendingComparator
 */
public class FontSizeLinesDescendingComparator implements Comparator<FontSizeAndLines> {

	@Override
	public int compare(final FontSizeAndLines o1, final FontSizeAndLines o2) {

		return o2.fontSize().compareTo(o1.fontSize());
	}

}
