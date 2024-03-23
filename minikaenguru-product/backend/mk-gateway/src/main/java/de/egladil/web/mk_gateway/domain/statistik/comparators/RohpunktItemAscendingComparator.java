// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.comparators;

import java.util.Comparator;

import de.egladil.web.mk_gateway.domain.statistik.RohpunktItem;

/**
 * RohpunktItemAscendingComparator
 */
public class RohpunktItemAscendingComparator implements Comparator<RohpunktItem> {

	@Override
	public int compare(final RohpunktItem o1, final RohpunktItem o2) {

		return o1.getPunkte() - o2.getPunkte();
	}

}
