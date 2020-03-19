// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import java.util.Comparator;

/**
 * KatalogItemNameComparator
 */
public class KatalogItemNameComparator implements Comparator<KatalogItem> {

	@Override
	public int compare(final KatalogItem o1, final KatalogItem o2) {

		if (o1 == o2) {

			return 0;
		}

		if (o1 == null && o2 != null) {

			return -1;
		}

		if (o1 != null && o2 == null) {

			return 1;
		}
		return new GermanStringComparator().compare(o1.getName(), o2.getName());
	}
}
