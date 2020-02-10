// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * KatalogItemNameComparator
 */
public class KatalogItemNameComparator implements Comparator<KatalogItem> {

	@Override
	public int compare(final KatalogItem o1, final KatalogItem o2) {

		Collator usCollator = Collator.getInstance(Locale.GERMAN); // Your locale here
		usCollator.setStrength(Collator.PRIMARY);
		return usCollator.compare(o1.getName(), o2.getName());
	}

}
