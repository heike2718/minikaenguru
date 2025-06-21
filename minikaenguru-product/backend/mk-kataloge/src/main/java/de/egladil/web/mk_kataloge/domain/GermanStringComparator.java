// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * GermanStringComparator
 */
public class GermanStringComparator implements Comparator<String> {

	@Override
	public int compare(final String o1, final String o2) {

		if (o1 == o2) {

			return 0;
		}

		if (o1 == null && o2 != null) {

			return -1;
		}

		if (o1 != null && o2 == null) {

			return 1;
		}
		Collator collator = Collator.getInstance(Locale.GERMAN);
		collator.setStrength(Collator.SECONDARY);
		return collator.compare(o1, o2);
	}

}
