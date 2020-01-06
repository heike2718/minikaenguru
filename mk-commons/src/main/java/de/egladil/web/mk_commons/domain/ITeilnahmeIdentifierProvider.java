// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain;

import de.egladil.web.mk_commons.domain.impl.TeilnahmeIdentifier;

/**
 * ITeilnahmeIdentifierProvider
 */
public interface ITeilnahmeIdentifierProvider {

	/**
	 * @return TeilnahmeIdentifier
	 */
	TeilnahmeIdentifier provideTeilnahmeIdentifier();

}
