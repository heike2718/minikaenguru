// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.comparators;

import java.util.Comparator;

import de.egladil.web.mk_gateway.domain.statistik.AufgabeErgebnisItem;

/**
 * AufgabeErgebnisItemComparator
 */
public class AufgabeErgebnisItemComparator implements Comparator<AufgabeErgebnisItem> {

	@Override
	public int compare(final AufgabeErgebnisItem o1, final AufgabeErgebnisItem o2) {

		return o1.getNummer().compareTo(o2.getNummer());
	}

}
