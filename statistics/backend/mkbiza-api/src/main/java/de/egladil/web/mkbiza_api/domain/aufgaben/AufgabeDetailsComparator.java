// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

import java.util.Comparator;

/**
 * AufgabeDetailsComparator
 */
public class AufgabeDetailsComparator implements Comparator<AufgabeDetails> {

	@Override
	public int compare(final AufgabeDetails o1, final AufgabeDetails o2) {

		return o1.getNummer().compareTo(o2.getNummer());
	}

}
