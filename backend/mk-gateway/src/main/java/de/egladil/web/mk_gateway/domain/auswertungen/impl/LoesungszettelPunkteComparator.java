// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen.impl;

import java.util.Comparator;

import de.egladil.web.mk_gateway.domain.auswertungen.Loesungszettel;

/**
 * LoesungszettelPunkteComparator sortiert aufsteigend nach Punkten.
 */
public class LoesungszettelPunkteComparator implements Comparator<Loesungszettel> {

	@Override
	public int compare(final Loesungszettel arg0, final Loesungszettel arg1) {

		return arg0.punkte() - arg1.punkte();
	}

}
