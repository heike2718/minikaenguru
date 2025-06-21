// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import de.egladil.web.mk_gateway.domain.kataloge.api.LandPayload;

/**
 * LandPayloadComparator
 */
public class LandPayloadComparator implements Comparator<LandPayload> {

	final Collator collator = Collator.getInstance(Locale.GERMANY);

	@Override
	public int compare(final LandPayload o1, final LandPayload o2) {

		return collator.compare(o1.name(), o2.name());
	}

}
