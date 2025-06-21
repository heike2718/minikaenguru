// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.statistik;

import java.util.Comparator;

/**
 * PunktintervallDescendingComparator sortiert die Punktintervalle absteigend.
 */
public class PunktintervallDescendingComparator implements Comparator<Punktintervall> {

	@Override
	public int compare(final Punktintervall arg0, final Punktintervall arg1) {

		return arg1.getMinVal() - arg0.getMinVal();
	}
}
