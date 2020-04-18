// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.teilnahmen;

import java.util.Optional;

import de.egladil.web.mk_wettbewerb.domain.model.wettbewerb.WettbewerbID;

/**
 * SchulteilnahmeRepository
 */
public interface SchulteilnahmeRepository {

	/**
	 * Sucht die Schulteilnahme anhand der WettbewerbID und es Schulkuerzels.
	 *
	 * @param  wettbewerbID
	 * @param  schulkuerzel
	 * @return              Optional einer Schulteilnahme
	 */
	Optional<Schulteilnahme> schulteilnahmeMitKuerzel(WettbewerbID wettbewerbID, Schulkuerzel schulkuerzel);

}
