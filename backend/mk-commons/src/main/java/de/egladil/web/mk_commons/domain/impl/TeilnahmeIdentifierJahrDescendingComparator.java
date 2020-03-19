// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import java.util.Comparator;

import de.egladil.web.mk_commons.domain.ITeilnahmeIdentifierProvider;

/**
 * TeilnahmeIdentifierJahrDescendingComparator
 */
public class TeilnahmeIdentifierJahrDescendingComparator implements Comparator<ITeilnahmeIdentifierProvider> {

	@Override
	public int compare(final ITeilnahmeIdentifierProvider o1, final ITeilnahmeIdentifierProvider o2) {

		int jahr1 = Integer.valueOf(o1.provideTeilnahmeIdentifier().getJahr());
		int jahr2 = Integer.valueOf(o2.provideTeilnahmeIdentifier().getJahr());

		return jahr2 - jahr1;
	}

}
