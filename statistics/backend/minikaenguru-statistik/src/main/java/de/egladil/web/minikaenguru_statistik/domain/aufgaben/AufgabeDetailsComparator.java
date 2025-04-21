// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain.aufgaben;

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
