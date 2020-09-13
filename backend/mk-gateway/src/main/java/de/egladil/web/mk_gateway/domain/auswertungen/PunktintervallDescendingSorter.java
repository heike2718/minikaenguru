// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.auswertungen;

import java.util.Comparator;

/**
 * PunktintervallDescendingSorter sortiert die Punktintervalle absteigend.
 */
public class PunktintervallDescendingSorter implements Comparator<Punktintervall> {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final Punktintervall arg0, final Punktintervall arg1) {

		return arg0.compareTo(arg1) * (-1);
	}
}
