// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import java.util.Comparator;

/**
 * VertragstextVersionComparator sortiert aufsteigend nach Versionsnummer.
 */
public class VertragstextVersionComparator implements Comparator<Vertragstext> {

	@Override
	public int compare(final Vertragstext o1, final Vertragstext o2) {

		return o1.versionsnummer().compareTo(o2.versionsnummer());
	}

}
