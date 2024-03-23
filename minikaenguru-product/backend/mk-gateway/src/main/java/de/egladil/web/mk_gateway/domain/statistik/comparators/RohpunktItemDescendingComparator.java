// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.comparators;

import java.util.Comparator;

import de.egladil.web.mk_gateway.domain.statistik.RohpunktItem;

/**
 * RohpunktItemDescendingComparator
 */
public class RohpunktItemDescendingComparator implements Comparator<RohpunktItem> {

	@Override
	public int compare(final RohpunktItem o1, final RohpunktItem o2) {

		return o2.getPunkte() - o1.getPunkte();
	}

}
