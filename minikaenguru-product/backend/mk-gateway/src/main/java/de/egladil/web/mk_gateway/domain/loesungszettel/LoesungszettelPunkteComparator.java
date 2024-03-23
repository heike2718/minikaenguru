// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.util.Comparator;

/**
 * LoesungszettelPunkteComparator sortiert aufsteigend nach Punkten.
 */
public class LoesungszettelPunkteComparator implements Comparator<Loesungszettel> {

	@Override
	public int compare(final Loesungszettel arg0, final Loesungszettel arg1) {

		return arg0.punkte() - arg1.punkte();
	}

}
