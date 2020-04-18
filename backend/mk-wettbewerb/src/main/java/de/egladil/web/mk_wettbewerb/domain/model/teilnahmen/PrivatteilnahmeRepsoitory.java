// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.teilnahmen;

import java.util.Optional;

import de.egladil.web.mk_wettbewerb.domain.model.wettbewerb.WettbewerbID;

/**
 * PrivatteilnahmeRepsoitory
 */
public interface PrivatteilnahmeRepsoitory {

	/**
	 * @param  wettbewerbID
	 *                      WettbewerbID
	 * @param  kuerzel
	 * @return              Optional einer Privatteilnahme
	 */
	Optional<Privatteilnahme> privatteilnahmeMitKuerzel(WettbewerbID wettbewerbID, Privatteilnahmekuerzel kuerzel);

}
