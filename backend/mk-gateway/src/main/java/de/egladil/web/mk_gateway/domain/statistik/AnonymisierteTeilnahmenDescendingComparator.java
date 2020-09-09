// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.Comparator;

import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.AnonymisierteTeilnahmeAPIModel;

/**
 * AnonymisierteTeilnahmenDescendingComparator
 */
public class AnonymisierteTeilnahmenDescendingComparator implements Comparator<AnonymisierteTeilnahmeAPIModel> {

	@Override
	public int compare(final AnonymisierteTeilnahmeAPIModel o1, final AnonymisierteTeilnahmeAPIModel o2) {

		return o2.identifier().jahr() - o1.identifier().jahr();
	}

}
