// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.comparators;

import java.util.Comparator;

import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungItem;
import de.egladil.web.mk_gateway.domain.statistik.PunktintervallDescendingComparator;

/**
 * GesamtpunktverteilungItemDescendingComparator
 */
public class GesamtpunktverteilungItemDescendingComparator implements Comparator<GesamtpunktverteilungItem> {

	private final PunktintervallDescendingComparator punktintervallDescendingComparator = new PunktintervallDescendingComparator();

	@Override
	public int compare(final GesamtpunktverteilungItem o1, final GesamtpunktverteilungItem o2) {

		return punktintervallDescendingComparator.compare(o1.getPunktintervall(), o2.getPunktintervall());
	}

}
