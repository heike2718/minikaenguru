// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb;

import java.util.Comparator;

/**
 * WettbewerbeDescendingComparator
 */
public class WettbewerbeDescendingComparator implements Comparator<Wettbewerb> {

	@Override
	public int compare(final Wettbewerb w1, final Wettbewerb w2) {

		return w2.id().jahr().intValue() - w1.id().jahr().intValue();
	}

}
