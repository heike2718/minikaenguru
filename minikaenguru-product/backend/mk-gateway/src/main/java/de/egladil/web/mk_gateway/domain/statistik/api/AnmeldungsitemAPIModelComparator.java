// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.api;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * AnmeldungsitemAPIModelComparator
 */
public class AnmeldungsitemAPIModelComparator implements Comparator<AnmeldungsitemAPIModel> {

	private final Collator collator = Collator.getInstance(Locale.GERMAN);

	@Override
	public int compare(final AnmeldungsitemAPIModel o1, final AnmeldungsitemAPIModel o2) {

		return collator.compare(o1.getName(), o2.getName());
	}

}
